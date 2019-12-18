package com.prowidesoftware.sandbox;

import com.prowidesoftware.swift.model.MtSwiftMessage;
import com.prowidesoftware.swift.model.MxId;
import com.prowidesoftware.swift.model.MxSwiftMessage;
import com.prowidesoftware.swift.model.field.Field20;
import com.prowidesoftware.swift.model.field.Field79;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103_STP;
import com.prowidesoftware.swift.model.mt.mt1xx.MT199;
import com.prowidesoftware.swift.model.mx.BusinessHeader;
import com.prowidesoftware.swift.model.mx.MxPacs00800102;
import com.prowidesoftware.swift.model.mx.MxType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initializes the memory database with a few simple MTs
 */
@Component
public class Seeder {

    @Autowired
    private MessageRepository repository;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // add several MT199

        for (int i=0; i<5; i++) {
            MT199 mt = new MT199();
            mt.append(Field20.tag("MYREFERENCE" + i));
            mt.append(Field79.tag("Narrative" + i));
            repository.save(new MtSwiftMessage(mt.getSwiftMessage()));
        }

        // add an MT103 sample

        MT103_STP mt = MT103_STP.parse("{1:F01ABCDJOC0AXXX0293022700}{2:I103ABCDJOC0XXXXN}{3:{103:JOD}{113:0112}{108:12345}{119:STP}}{4:\n" +
                ":20:12345\n" +
                ":23B:CRED\n" +
                ":26T:001\n" +
                ":32A:190110JOD1000,\n" +
                ":33B:JOD10000,\n" +
                ":50K:/987654321\n" +
                "MINISTRY OF FINANCE COLLECTED REVEN\n" +
                "BR CENTER\n" +
                ":59:/876543219\n" +
                "MINISTRY OF FINANCE COLLECTED REVEN\n" +
                "NEW YORK USA\n" +
                ":70:0101\n" +
                "INVOICE PAYMENT AND PURCHASE\n" +
                ":71A:OUR\n" +
                "-}");
        mt.getSwiftMessage().setUETR();
        repository.save(new MtSwiftMessage(mt.getSwiftMessage()));

        // add a couple of MX messages with different header flavor

        String xml = "<Document xmlns='urn:iso:std:iso:20022:tech:xsd:pacs.008.001.02'\n" +
                "\t\txmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>\n" +
                "\t\t<FIToFICstmrCdtTrf>\n" +
                "\t\t\t<GrpHdr>\n" +
                "\t\t\t\t<MsgId>MSG112233</MsgId>\n" +
                "\t\t\t\t<CreDtTm>2017-08-14T21:38:55</CreDtTm>\n" +
                "\t\t\t\t<NbOfTxs>1</NbOfTxs>\n" +
                "\t\t\t\t<CtrlSum>1</CtrlSum>\n" +
                "\t\t\t\t<TtlIntrBkSttlmAmt Ccy='USD'>1</TtlIntrBkSttlmAmt>\n" +
                "\t\t\t\t<IntrBkSttlmDt>2018-06-14</IntrBkSttlmDt>\n" +
                "\t\t\t\t<SttlmInf>\n" +
                "\t\t\t\t\t<SttlmMtd>INDA</SttlmMtd>\n" +
                "\t\t\t\t</SttlmInf>\n" +
                "\t\t\t\t<InstgAgt>\n" +
                "\t\t\t\t\t<FinInstnId>\n" +
                "\t\t\t\t\t\t<BIC>DTCCUS3NXXX</BIC>\n" +
                "\t\t\t\t\t\t<ClrSysMmbId>\n" +
                "\t\t\t\t\t\t\t<MmbId>12121212</MmbId>\n" +
                "\t\t\t\t\t\t</ClrSysMmbId>\n" +
                "\t\t\t\t\t\t<Nm>AAAAA</Nm>\n" +
                "\t\t\t\t\t</FinInstnId>\n" +
                "\t\t\t\t</InstgAgt>\n" +
                "\t\t\t\t<InstdAgt>\n" +
                "\t\t\t\t\t<FinInstnId>\n" +
                "\t\t\t\t\t\t<BIC>DTCCUS3NXXX</BIC>\n" +
                "\t\t\t\t\t\t<Nm>AAAAaa</Nm>\n" +
                "\t\t\t\t\t</FinInstnId>\n" +
                "\t\t\t\t</InstdAgt>\n" +
                "\t\t\t</GrpHdr>\n" +
                "\t\t\t<CdtTrfTxInf>\n" +
                "\t\t\t\t<PmtId>\n" +
                "\t\t\t\t\t<InstrId>12345678</InstrId>\n" +
                "\t\t\t\t\t<EndToEndId>ID123456</EndToEndId>\n" +
                "\t\t\t\t\t<TxId>TID123456</TxId>\n" +
                "\t\t\t\t</PmtId>\n" +
                "\t\t\t\t<IntrBkSttlmAmt Ccy='USD'>1</IntrBkSttlmAmt>\n" +
                "\t\t\t\t<IntrBkSttlmDt>2017-08-14</IntrBkSttlmDt>\n" +
                "\t\t\t\t<SttlmPrty>URGT</SttlmPrty>\n" +
                "\t\t\t\t<InstdAmt Ccy='ARS'>1</InstdAmt>\n" +
                "\t\t\t\t<XchgRate>1</XchgRate>\n" +
                "\t\t\t\t<ChrgBr>SLEV</ChrgBr>\n" +
                "\t\t\t\t<Dbtr>\n" +
                "\t\t\t\t\t<Nm>AAAAAAA</Nm>\n" +
                "\t\t\t\t</Dbtr>\n" +
                "\t\t\t\t<DbtrAgt>\n" +
                "\t\t\t\t\t<FinInstnId>\n" +
                "\t\t\t\t\t\t<BIC>DTCCUS3NXXX</BIC>\n" +
                "\t\t\t\t\t</FinInstnId>\n" +
                "\t\t\t\t</DbtrAgt>\n" +
                "\t\t\t\t<CdtrAgt>\n" +
                "\t\t\t\t\t<FinInstnId>\n" +
                "\t\t\t\t\t\t<BIC>DTCCUS3NXXX</BIC>\n" +
                "\t\t\t\t\t</FinInstnId>\n" +
                "\t\t\t\t</CdtrAgt>\n" +
                "\t\t\t\t<Cdtr>\n" +
                "\t\t\t\t\t<Nm>BBBBBBB</Nm>\n" +
                "\t\t\t\t</Cdtr>\n" +
                "\t\t\t</CdtTrfTxInf>\n" +
                "\t\t</FIToFICstmrCdtTrf>\n" +
                "\t</Document>";

        MxPacs00800102 mx1 = MxPacs00800102.parse(xml);
        mx1.setBusinessHeader(BusinessHeader.create("SENDUSXXXXX", "RECVFRXXXXX", "BAHV10", MxType.pacs_008_001_02.mxId()));
        repository.save(new MxSwiftMessage(mx1));

        MxPacs00800102 mx2 = MxPacs00800102.parse(xml);
        mx2.setBusinessHeader(new BusinessHeader(BusinessHeader.createApplicationHeader("SENDUSXXXXX", "RECVFRXXXXX", "AH111", MxType.pacs_008_001_02.mxId())));
        repository.save(new MxSwiftMessage(mx2));
    }

}
