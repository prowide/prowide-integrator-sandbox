/*******************************************************************************
 * Copyright (c) 2016 Prowide Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of private license agreements
 * between Prowide Inc. and its commercial customers and partners.
 *******************************************************************************/
package com.prowidesoftware.sandbox;

import com.prowidesoftware.ProwideException;
import com.prowidesoftware.swift.guitools.FormBuilder;
import com.prowidesoftware.swift.guitools.MtFormBuilder;
import com.prowidesoftware.swift.guitools.MxFormBuilder;
import com.prowidesoftware.swift.model.AbstractSwiftMessage;
import com.prowidesoftware.swift.model.MtSwiftMessage;
import com.prowidesoftware.swift.model.MxSwiftMessage;
import com.prowidesoftware.swift.model.mt.MtType;
import com.prowidesoftware.swift.model.mx.MxType;
import com.prowidesoftware.swift.validator.ValidationEngine;
import com.prowidesoftware.swift.validator.ValidationProblem;
import com.prowidesoftware.swift.validator.ValidationResult;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class SandboxController {
	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private MessageRepository repository;

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("messages", repository.findAll());
		return "index";
	}

	private MtFormBuilder createMtBuilder() {
		MtFormBuilder builder = new MtFormBuilder();
		/*
		 * The form builder supports several rendering and behaviour configurations.
		 * For example you can use the line below to enable arabic characters, overwriting the default restrictions
		 * that only admits SWIFT character sets
		 */
		//builder.getConfig().addCharacterRangeExtension(UnicodeBlockRange.Arabic);
		return builder;
	}

	private MxFormBuilder createMxBuilder() {
		MxFormBuilder builder = new MxFormBuilder();
		//builder.getConfig().addCharacterRangeExtension(UnicodeBlockRange.Arabic);
		return builder;
	}

	/**
	 * Display a list of message types for selection
	 *
	 * We use the specific enums for MT or MX to generate a list.
	 */
	@GetMapping("/selection/{standard}")
	public ModelAndView messageSelection(@PathVariable String standard) {
		ModelAndView mv = new ModelAndView("selection");
		List<Pair> messages = new ArrayList<>();

		if (StringUtils.equals("mx", standard)) {
			// create list for all MX types
			for (MxType type : MxType.values()) {
				String name = StringUtils.replace(type.name(), "_", ".");
				messages.add(new Pair("/create/mx/" + type.name(), name));
			}
		} else {
			// create list for all MT types but system messages
			for (MtType type : MtType.values()) {
				if (!StringUtils.equals("0", type.category())) {
					messages.add(new Pair("/create/mt/" + type.name(), type.nameFormatted()));
				}
			}
		}

		mv.addObject("messages", messages);
		mv.addObject("standard", standard.toUpperCase());
		return mv;
	}

	/**
	 * Display a form for that specific message type
	 */
    @GetMapping("/create/{standard}/{type}")
    protected ModelAndView messageForm(@PathVariable String standard, @PathVariable String type) throws IOException {
    	ModelAndView mv = new ModelAndView("form");
    	mv.addObject("type", type);
		mv.addObject("standard", standard);

		StringWriter out = new StringWriter();

		if (StringUtils.equals("mx", standard)) {
			createMxBuilder().writeMXForm(MxType.valueOf(type), out, null);
		} else {
			createMtBuilder().writeMTForm(MtType.valueOf(type), out, null);
		}

		mv.addObject("form", out);
		return mv;
    }

	/**
	 * To test or debug message repairing from hardcoded message samples
	 */
	@GetMapping("/repair/test")
	protected ModelAndView messageRepairDebug() throws IOException {
		MtSwiftMessage mt = MtSwiftMessage.parse("{1:F01ABCDJOC0AXXX0293022700}{2:I103ABCDJOC0XXXXN}{3:{103:JOD}{113:0112}{108:12345}{119:STP}}{4:\n" +
				":20:12345\n" +
				":23B:CRED\n" +
				":26T:001\n" +
				":32A:190110JOD1000,\n" +
				":33B:JOD10000,\n" +
				":50K:/987654321\n" +
				"FOO OF FINANCE COLLECTED REVEN\n" +
				"BR CENTER\n" +
				":59:/876543219\n" +
				"FOO OF FINANCE COLLECTED REVEN2\n" +
				"NEW YORK USA\n" +
				":70:0101\n" +
				"INVOICE PAYMENT AND PURCHASE\n" +
				":71A:OUR\n" +
				"-}");
		ModelAndView mv = new ModelAndView("form");
		StringWriter out = new StringWriter();
		createMtBuilder().writeMTForm(MtType.valueOf(mt.getMtId()), out, mt);
		mv.addObject("form", out);
		return mv;
	}

	/**
	 * Display a form for that persisted message given its id
	 */
	@GetMapping("/repair/{id}")
	protected ModelAndView messageRepair(@PathVariable Long id) throws IOException {
		AbstractSwiftMessage msg = repository.findById(id).orElseThrow(() -> new ProwideException("Message with id=" + id + "not found"));
		ModelAndView mv = new ModelAndView("form");
		StringWriter out = new StringWriter();

		if (msg.isMX()) {
			mv.addObject("standard", "mx");
			MxSwiftMessage mx = (MxSwiftMessage) msg;
			createMxBuilder().writeMXForm(MxType.valueOf(mx.getIdentifier()), out, mx);

		} else {
			mv.addObject("standard", "mt");
			MtSwiftMessage mt = (MtSwiftMessage) msg;
			createMtBuilder().writeMTForm(MtType.valueOf(mt.getMtId()), out, mt);
		}

		mv.addObject("form", out);
		return mv;
	}

	/**
	 * Display a form for that persisted message given its id
	 */
	@GetMapping("/detail/{id}")
	protected ModelAndView messageDetail(@PathVariable Long id) {
		AbstractSwiftMessage msg = repository.findById(id).orElseThrow(() -> new ProwideException("Message with id=" + id + " not found"));
		ModelAndView mv = new ModelAndView("detail");

		FormBuilder builder;
		if (msg.isMX()) {
			mv.addObject("standard", "mx");
			builder = createMxBuilder();
		} else {
			mv.addObject("standard", "mt");
			builder = createMtBuilder();
		}
		StringWriter out = new StringWriter();
		builder.writeDetail(out, msg);

		mv.addObject("detail", out);
		mv.addObject("id", msg.getId());
		return mv;
	}

    @PostMapping("/{standard}")
    protected String messageCreation(@PathVariable String standard, HttpServletRequest req) {

		// get the id to check if this is a new message creation or an existing message repair
		final String id = req.getParameter("id");
		AbstractSwiftMessage msg;

		if (StringUtils.isNotBlank(id)) {
			// we are updating an existing message
			msg = repository.findById(new Long(id)).orElseThrow(() -> new ProwideException("Message with id=" + id + " not found"));
			if (StringUtils.equals("mx", standard)) {
				MxFormBuilder.map(req, (MxSwiftMessage) msg);
			} else {
				MtFormBuilder.map(req, (MtSwiftMessage) msg);
			}
		} else {
			// we are creating a new message
			if (StringUtils.equals("mx", standard)) {
				msg = MxFormBuilder.map(req);
			} else {
				msg = MtFormBuilder.map(req);
			}
		}
		log.info("mapped message: "+msg);

		/*
		 * On a real application here you might call this backend validation from the form via ajax in order to check
		 * the created message is full standard compliance, sending the backend validation result back to the form in
		 * case of errors.
		 * Notice the client side validation from the GUI Tools library is just a lightweight javascript check of the
		 * mandatory fields and content charsets. That client side validation does not check for example
		 * network/semantic rules which are validated using the ValidationEngine class from the Prowide Integrator
		 * validation module.
		 */
		ValidationEngine e = new ValidationEngine();
		// If you enable character set extensions in the form configuration, you should switch off the character
		// validation here accordingly
		// e.getConfig().addIgnoredErrorKey(FieldProblem.T33.name());
		List<ValidationProblem> p;
		if (msg.isMT()) {
			p = e.validateMtMessage(msg.message());
		} else {
			p = e.validateMxMessage(msg.message());
		}
		ValidationResult result = new ValidationResult(p);
		if (!result.isValid()) {
			log.info(ValidationProblem.printout(p));
			// this JSON could be sent as service response
			log.info(result.toJson(Locale.ENGLISH));
		}
		e.dispose();

		// save the created message in the database
		repository.save(msg);
		log.info("saved message id="+msg.getId());

		// redirect to the detail page.
		return "redirect:/detail/" + msg.getId();
    }

	@GetMapping("/test")
	protected ModelAndView messageDetailTest() {
		String mt = "{1:F01ABCDJOC0AXXX0293022700}{2:I103ABCDJOC0XXXXN}{3:{103:JOD}{113:0112}{108:12345}{119:STP}{121:02eb4348-aeba-433a-9570-d597d38a28ba}}{4:\n" +
				":20:12345ساسي\n" +
				":23B:CRED\n" +
				":26T:001\n" +
				":32A:190110JOD1000,\n" +
				":33B:JOD10000,\n" +
				":50K:/987654321\n" +
				"MINISTRY OF FINANCE COLLECTED REVEN\n" +
				"BR CENTER\n" +
				"شسيبس\n" +
				":59:/876543219\n" +
				"MINISTRY OF FINANCE COLLECTED REVEN\n" +
				"NEW YORK USA\n" +
				":70:0101\n" +
				"INVOICE PAYMENT AND PURCHASE\n" +
				":71A:OUR\n" +
				"-}";

		ModelAndView mv = new ModelAndView("detail");

		mv.addObject("standard", "mt");
		FormBuilder builder = createMtBuilder();
		StringWriter out = new StringWriter();
		MtSwiftMessage msg = new MtSwiftMessage(mt);
		builder.writeDetail(out, msg);

		mv.addObject("detail", out);
		mv.addObject("id", msg.getId());
		return mv;
	}

}