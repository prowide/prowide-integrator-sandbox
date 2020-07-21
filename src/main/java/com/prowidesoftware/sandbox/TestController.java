package com.prowidesoftware.sandbox;

import com.prowidesoftware.swift.guitools.FormBuilder;
import com.prowidesoftware.swift.guitools.MtFormBuilder;
import com.prowidesoftware.swift.model.MtSwiftMessage;
import com.prowidesoftware.swift.model.mt.MtType;
import com.prowidesoftware.swift.validator.ValidationEngine;
import com.prowidesoftware.swift.validator.ValidationProblem;
import com.prowidesoftware.utils.UnicodeBlockRange;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * End points for hardcoded test sample scenarios
 */
@Controller
public class TestController {

    /**
     * To test or debug message repairing from hardcoded message samples
     */
    @GetMapping("/test/repair")
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
        new MtFormBuilder().writeMTForm(MtType.valueOf(mt.getMtId()), out, mt);
        mv.addObject("form", out);
        return mv;
    }

    @GetMapping("/test/detail")
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
        MtFormBuilder builder = new MtFormBuilder();
        builder.getConfig().addCharacterRangeExtension(UnicodeBlockRange.Arabic);
        StringWriter out = new StringWriter();
        MtSwiftMessage msg = new MtSwiftMessage(mt);
        builder.writeDetail(out, msg);

        mv.addObject("detail", out);
        mv.addObject("id", msg.getId());
        return mv;
    }

    @GetMapping("/test/validate")
    @ResponseBody
    public String test() {
        ValidationEngine e = new ValidationEngine();
        e.initialize();
        List<ValidationProblem> result = e.validateMxMessage(xml);

        StringBuilder response = new StringBuilder();
        for (ValidationProblem problem : result) {
            response.append(problem.getMessage());
        }
        return response.toString();
    }

    private String xml = "<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n" +
            "<Document xmlns = \"urn:iso:std:iso:20022:tech:xsd:pain.001.001.02\" xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "\t<pain.001.001.02>\n" +
            "\t\t<GrpHdr>\n" +
            "\t\t\t<MsgId>ABC/060928/CCT001</MsgId>\n" +
            "\t\t\t<CreDtTm>2006-09-28T14:07:00</CreDtTm>\n" +
            "\t\t\t<BtchBookg>false</BtchBookg>\n" +
            "\t\t\t<NbOfTxs>3</NbOfTxs>\n" +
            "\t\t\t<CtrlSum>11500000</CtrlSum>\n" +
            "\t\t\t<Grpg>GRPD</Grpg>\n" +
            "\t\t\t<InitgPty>\n" +
            "\t\t\t\t<Nm>ABC Corporation</Nm>\n" +
            "\t\t\t\t<PstlAdr>\n" +
            "\t\t\t\t\t<StrtNm>Times Square</StrtNm>\n" +
            "\t\t\t\t\t<BldgNb>7</BldgNb>\n" +
            "\t\t\t\t\t<PstCd>NY 10036</PstCd>\n" +
            "\t\t\t\t\t<TwnNm>New York</TwnNm>\n" +
            "\t\t\t\t\t<Ctry>US</Ctry>\n" +
            "\t\t\t\t</PstlAdr>\n" +
            "\t\t\t</InitgPty>\n" +
            "\t\t</GrpHdr>\n" +
            "\t\t<PmtInf>\n" +
            "\t\t\t<PmtMtd>TRF</PmtMtd>\n" +
            "\t\t\t<ReqdExctnDt>2006-09-29</ReqdExctnDt>\n" +
            "\t\t\t<Dbtr>\n" +
            "\t\t\t\t<Nm>ABC Corporation</Nm>\n" +
            "\t\t\t\t<PstlAdr>\n" +
            "\t\t\t\t\t<StrtNm>Times Square</StrtNm>\n" +
            "\t\t\t\t\t<BldgNb>7</BldgNb>\n" +
            "\t\t\t\t\t<PstCd>NY 10036</PstCd>\n" +
            "\t\t\t\t\t<TwnNm>New York</TwnNm>\n" +
            "\t\t\t\t\t<Ctry>US</Ctry>\n" +
            "\t\t\t\t</PstlAdr>\n" +
            "\t\t\t</Dbtr>\n" +
            "\t\t\t<DbtrAcct>\n" +
            "\t\t\t\t<Id>\n" +
            "\t\t\t\t\t<PrtryAcct>\n" +
            "\t\t\t\t\t\t<Id>00125574999</Id>\n" +
            "\t\t\t\t\t</PrtryAcct>\n" +
            "\t\t\t\t</Id>\n" +
            "\t\t\t</DbtrAcct>\n" +
            "\t\t\t<DbtrAgt>\n" +
            "\t\t\t\t<FinInstnId>\n" +
            "\t\t\t\t\t<BIC>BBBBUS33</BIC>\n" +
            "\t\t\t\t</FinInstnId>\n" +
            "\t\t\t</DbtrAgt>\n" +
            "\t\t\t<CdtTrfTxInf>\n" +
            "\t\t\t\t<PmtId>\n" +
            "\t\t\t\t\t<InstrId>ABC/060928/CCT001/1</InstrId>\n" +
            "\t\t\t\t\t<EndToEndId>ABC/4562/2006-09-08</EndToEndId>\n" +
            "\t\t\t\t</PmtId>\n" +
            "\t\t\t\t<Amt>\n" +
            "\t\t\t\t\t<InstdAmt Ccy = \"JPY\">10000000</InstdAmt>\n" +
            "\t\t\t\t</Amt>\n" +
            "\t\t\t\t<ChrgBr>SHAR</ChrgBr>\n" +
            "\t\t\t\t<CdtrAgt>\n" +
            "\t\t\t\t\t<FinInstnId>\n" +
            "\t\t\t\t\t\t<BIC>ABCDE</BIC>\n" +
            "\t\t\t\t\t</FinInstnId>\n" +
            "\t\t\t\t</CdtrAgt>\n" +
            "\t\t\t\t<Cdtr>\n" +
            "\t\t\t\t\t<Nm>DEF Electronics</Nm>\n" +
            "\t\t\t\t\t<PstlAdr>\n" +
            "\t\t\t\t\t\t<AdrLine>Corn Exchange 5th Floor</AdrLine>\n" +
            "\t\t\t\t\t\t<StrtNm>Mark Lane</StrtNm>\n" +
            "\t\t\t\t\t\t<BldgNb>55</BldgNb>\n" +
            "\t\t\t\t\t\t<PstCd>EC3R7NE</PstCd>\n" +
            "\t\t\t\t\t\t<TwnNm>London</TwnNm>\n" +
            "\t\t\t\t\t\t<Ctry>GB</Ctry>\n" +
            "\t\t\t\t\t</PstlAdr>\n" +
            "\t\t\t\t</Cdtr>\n" +
            "\t\t\t\t<CdtrAcct>\n" +
            "\t\t\t\t\t<Id>\n" +
            "\t\t\t\t\t\t<PrtryAcct>\n" +
            "\t\t\t\t\t\t\t<Id>23683707994215</Id>\n" +
            "\t\t\t\t\t\t</PrtryAcct>\n" +
            "\t\t\t\t\t</Id>\n" +
            "\t\t\t\t</CdtrAcct>\n" +
            "\t\t\t\t<Purp>\n" +
            "\t\t\t\t\t<Cd>GDDS</Cd>\n" +
            "\t\t\t\t</Purp>\n" +
            "\t\t\t\t<RmtInf>\n" +
            "\t\t\t\t\t<Strd>\n" +
            "\t\t\t\t\t\t<RfrdDocInf>\n" +
            "\t\t\t\t\t\t\t<RfrdDocTp>\n" +
            "\t\t\t\t\t\t\t\t<Cd>CINV</Cd>\n" +
            "\t\t\t\t\t\t\t</RfrdDocTp>\n" +
            "\t\t\t\t\t\t\t<RfrdDocNb>4562</RfrdDocNb>\n" +
            "\t\t\t\t\t\t</RfrdDocInf>\n" +
            "\t\t\t\t\t\t<RfrdDocRltdDt>2006-09-08</RfrdDocRltdDt>\n" +
            "\t\t\t\t\t</Strd>\n" +
            "\t\t\t\t</RmtInf>\n" +
            "\t\t\t</CdtTrfTxInf>\n" +
            "\t\t\t<CdtTrfTxInf>\n" +
            "\t\t\t\t<PmtId>\n" +
            "\t\t\t\t\t<InstrId>ABC/060928/CCT001/2</InstrId>\n" +
            "\t\t\t\t\t<EndToEndId>ABC/ABC-13679/2006-09-15</EndToEndId>\n" +
            "\t\t\t\t</PmtId>\n" +
            "\t\t\t\t<Amt>\n" +
            "\t\t\t\t\t<InstdAmt Ccy = \"EUR\">500000</InstdAmt>\n" +
            "\t\t\t\t</Amt>\n" +
            "\t\t\t\t<ChrgBr>CRED</ChrgBr>\n" +
            "\t\t\t\t<CdtrAgt>\n" +
            "\t\t\t\t\t<FinInstnId>\n" +
            "\t\t\t\t\t\t<BIC>DDDDBEBB</BIC>\n" +
            "\t\t\t\t\t</FinInstnId>\n" +
            "\t\t\t\t</CdtrAgt>\n" +
            "\t\t\t\t<Cdtr>\n" +
            "\t\t\t\t\t<Nm>GHI Semiconductors</Nm>\n" +
            "\t\t\t\t\t<PstlAdr>\n" +
            "\t\t\t\t\t\t<StrtNm>Avenue Brugmann</StrtNm>\n" +
            "\t\t\t\t\t\t<BldgNb>415</BldgNb>\n" +
            "\t\t\t\t\t\t<PstCd>1180</PstCd>\n" +
            "\t\t\t\t\t\t<TwnNm>Brussels</TwnNm>\n" +
            "\t\t\t\t\t\t<Ctry>BE</Ctry>\n" +
            "\t\t\t\t\t</PstlAdr>\n" +
            "\t\t\t\t</Cdtr>\n" +
            "\t\t\t\t<CdtrAcct>\n" +
            "\t\t\t\t\t<Id>\n" +
            "\t\t\t\t\t\t<IBAN>BE30001216371411</IBAN>\n" +
            "\t\t\t\t\t</Id>\n" +
            "\t\t\t\t</CdtrAcct>\n" +
            "\t\t\t\t<InstrForCdtrAgt>\n" +
            "\t\t\t\t\t<Cd>PHOB</Cd>\n" +
            "\t\t\t\t\t<InstrInf>+32/2/2222222</InstrInf>\n" +
            "\t\t\t\t</InstrForCdtrAgt>\n" +
            "\t\t\t\t<Purp>\n" +
            "\t\t\t\t\t<Cd>GDDS</Cd>\n" +
            "\t\t\t\t</Purp>\n" +
            "\t\t\t\t<RmtInf>\n" +
            "\t\t\t\t\t<Strd>\n" +
            "\t\t\t\t\t\t<RfrdDocInf>\n" +
            "\t\t\t\t\t\t\t<RfrdDocTp>\n" +
            "\t\t\t\t\t\t\t\t<Cd>CINV</Cd>\n" +
            "\t\t\t\t\t\t\t</RfrdDocTp>\n" +
            "\t\t\t\t\t\t\t<RfrdDocNb>ABC-13679</RfrdDocNb>\n" +
            "\t\t\t\t\t\t</RfrdDocInf>\n" +
            "\t\t\t\t\t\t<RfrdDocRltdDt>2006-09-15</RfrdDocRltdDt>\n" +
            "\t\t\t\t\t</Strd>\n" +
            "\t\t\t\t</RmtInf>\n" +
            "\t\t\t</CdtTrfTxInf>\n" +
            "\t\t\t<CdtTrfTxInf>\n" +
            "\t\t\t\t<PmtId>\n" +
            "\t\t\t\t\t<InstrId>ABC/060928/CCT001/3</InstrId>\n" +
            "\t\t\t\t\t<EndToEndId>ABC/987-AC/2006-09-27</EndToEndId>\n" +
            "\t\t\t\t</PmtId>\n" +
            "\t\t\t\t<Amt>\n" +
            "\t\t\t\t\t<InstdAmt Ccy = \"USD\">1000000</InstdAmt>\n" +
            "\t\t\t\t</Amt>\n" +
            "\t\t\t\t<ChrgBr>SHAR</ChrgBr>\n" +
            "\t\t\t\t<CdtrAgt>\n" +
            "\t\t\t\t\t<FinInstnId>\n" +
            "\t\t\t\t\t\t<BIC>BBBBUS66</BIC>\n" +
            "\t\t\t\t\t</FinInstnId>\n" +
            "\t\t\t\t</CdtrAgt>\n" +
            "\t\t\t\t<Cdtr>\n" +
            "\t\t\t\t\t<Nm>ABC Corporation</Nm>\n" +
            "\t\t\t\t\t<PstlAdr>\n" +
            "\t\t\t\t\t\t<StrtNm>Bush Street</StrtNm>\n" +
            "\t\t\t\t\t\t<BldgNb>13</BldgNb>\n" +
            "\t\t\t\t\t\t<PstCd>CA 94108</PstCd>\n" +
            "\t\t\t\t\t\t<TwnNm>San Francisco</TwnNm>\n" +
            "\t\t\t\t\t\t<Ctry>US</Ctry>\n" +
            "\t\t\t\t\t</PstlAdr>\n" +
            "\t\t\t\t</Cdtr>\n" +
            "\t\t\t\t<CdtrAcct>\n" +
            "\t\t\t\t\t<Id>\n" +
            "\t\t\t\t\t\t<PrtryAcct>\n" +
            "\t\t\t\t\t\t\t<Id>4895623</Id>\n" +
            "\t\t\t\t\t\t</PrtryAcct>\n" +
            "\t\t\t\t\t</Id>\n" +
            "\t\t\t\t</CdtrAcct>\n" +
            "\t\t\t\t<Purp>\n" +
            "\t\t\t\t\t<Cd>INTC</Cd>\n" +
            "\t\t\t\t</Purp>\n" +
            "\t\t\t\t<RmtInf>\n" +
            "\t\t\t\t\t<Strd>\n" +
            "\t\t\t\t\t\t<RfrdDocInf>\n" +
            "\t\t\t\t\t\t\t<RfrdDocTp>\n" +
            "\t\t\t\t\t\t\t\t<Cd>CINV</Cd>\n" +
            "\t\t\t\t\t\t\t</RfrdDocTp>\n" +
            "\t\t\t\t\t\t\t<RfrdDocNb>987-AC</RfrdDocNb>\n" +
            "\t\t\t\t\t\t</RfrdDocInf>\n" +
            "\t\t\t\t\t\t<RfrdDocRltdDt>2006-09-27</RfrdDocRltdDt>\n" +
            "\t\t\t\t\t</Strd>\n" +
            "\t\t\t\t</RmtInf>\n" +
            "\t\t\t</CdtTrfTxInf>\n" +
            "\t\t</PmtInf>\n" +
            "\t</pain.001.001.02>\n" +
            "</Document>";
}
