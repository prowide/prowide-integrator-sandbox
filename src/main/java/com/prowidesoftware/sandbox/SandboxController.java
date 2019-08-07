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
			MxFormBuilder builder = new MxFormBuilder();
			builder.writeMXForm(MxType.valueOf(type), out, null);
		} else {
			MtFormBuilder builder = new MtFormBuilder();
			builder.writeMTForm(MtType.valueOf(type), out, null);
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
		MtFormBuilder builder = new MtFormBuilder();
		builder.writeMTForm(MtType.valueOf(mt.getMtId()), out, mt);
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
			MxFormBuilder builder = new MxFormBuilder();
			builder.writeMXForm(MxType.valueOf(mx.getIdentifier()), out, mx);

		} else {
			mv.addObject("standard", "mt");
			MtSwiftMessage mt = (MtSwiftMessage) msg;
			MtFormBuilder builder = new MtFormBuilder();
			builder.writeMTForm(MtType.valueOf(mt.getMtId()), out, mt);
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
			builder = new MxFormBuilder();
		} else {
			mv.addObject("standard", "mt");
			builder = new MtFormBuilder();
		}
		StringWriter out = new StringWriter();
		builder.writeDetail(out, msg);

		mv.addObject("detail", out);
		mv.addObject("id", msg.getId());
		return mv;
	}

    @PostMapping("/{standard}")
    protected String messageCreation(@PathVariable String standard, HttpServletRequest req) {
		AbstractSwiftMessage msg;
		if (StringUtils.equals("mx", standard)) {
			msg = MxFormBuilder.map(req);
		} else {
			msg = MtFormBuilder.map(req);
		}
		log.info("mapped message: "+msg);

		/*
		 * On a real application here you should be calling the validation engine
		 * (from Prowide Integrator Validation module) in order to check the created
		 * message is full standard compliance, sending the backend validation result
		 * back to the form in case of errors. Notice the client side validation done
		 * in the form is just a lightweight javascript check on mandatory fields and
		 * content. That client side validation (included in the GUI Tools module) does
		 * not check for example network/semantic rules.
		 */

		// save the creted message in the database
		repository.save(msg);
		log.info("saved message id="+msg.getId());

		// redirect to the detail page.
		return "redirect:/detail/" + msg.getId();
    }

}