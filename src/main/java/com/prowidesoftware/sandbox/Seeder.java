package com.prowidesoftware.sandbox;

import com.prowidesoftware.swift.model.MtSwiftMessage;
import com.prowidesoftware.swift.model.MxSwiftMessage;
import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.SwiftMessageFactory;
import com.prowidesoftware.swift.model.field.Field20;
import com.prowidesoftware.swift.model.field.Field79;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103_STP;
import com.prowidesoftware.swift.model.mt.mt1xx.MT199;
import com.prowidesoftware.swift.model.mt.mt3xx.MT320;
import com.prowidesoftware.swift.model.mt.mt5xx.MT535;
import com.prowidesoftware.swift.model.mx.AppHdrFactory;
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

        SwiftMessage mtoutput = SwiftMessageFactory.toggleDirection(mt.getSwiftMessage());
        mtoutput.getBlock4().getTagByName(Field20.NAME).setValue("OUT1234");
        repository.save(new MtSwiftMessage(mtoutput));

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
        mx1.setAppHdr(AppHdrFactory.createBusinessAppHdrV01("SENDUSXXXXX", "RECVFRXXXXX", "BAHV10", MxType.pacs_008_001_02.mxId()));
        repository.save(new MxSwiftMessage(mx1));

        MxPacs00800102 mx2 = MxPacs00800102.parse(xml);
        mx2.setAppHdr(AppHdrFactory.createLegacyAppHdr("SENDUSXXXXX", "RECVFRXXXXX", "AH111", MxType.pacs_008_001_02.mxId()));
        repository.save(new MxSwiftMessage(mx2));

        MT320 mt320 = MT320.parse("{1:F01AAAAKWK0AXXX8464000001}{2:I320BBBBKWKWXFXSN}{4:\n" +
                ":15A:\n" +
                ":20:TSY/B712345\n" +
                ":22A:NEWT\n" +
                ":22B:CONF\n" +
                ":22C:BBBBK00000BBBBKW\n" +
                ":82A:BBBBKWKWXXX\n" +
                ":87A:BBBBKWKW\n" +
                ":15B:\n" +
                ":17R:B\n" +
                ":30T:20200707\n" +
                ":30V:20200708\n" +
                ":30P:20201008\n" +
                ":32B:EUR8,00\n" +
                ":30X:20201008\n" +
                ":34E:EUR0,00\n" +
                ":37G:0,00\n" +
                ":14D:ACT/360\n" +
                ":15C:\n" +
                ":53A:BBBBDEFF\n" +
                ":57A:BBBBDEFFXXX\n" +
                ":15D:\n" +
                ":57A:BBBBDEFF\n" +
                "-}");
        repository.save(new MtSwiftMessage(mt320));

        // large message
        MT535 mt535 = MT535.parse("{1:F01AAAAKWKWAXXX3733403174}{2:I535ABCDJOC0XXXXN}{4:\n" +
                ":16R:GENL\n" +
                ":28E:1/MORE\n" +
                ":20C::SEME//LARGMSG1234\n" +
                ":23G:NEWM\n" +
                ":98C::STAT//20200623180000\n" +
                ":22F::SFRE//DAIL\n" +
                ":22F::CODE//COMP\n" +
                ":22F::STTY//ACCT\n" +
                ":22F::STBA//SETT\n" +
                ":97A::SAFE//12345\n" +
                ":17B::ACTI//Y\n" +
                ":17B::CONS//N\n" +
                ":16S:GENL\n" +
                ":16R:SUBSAFE\n" +
                ":16R:FIN\n" +
                ":35B:ISIN US12345RAL06\n" +
                "//FOOBAR    20/08\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/106,795000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10679500,\n" +
                ":19A::ACRU//USD133250,\n" +
                ":70E::HOLD//20190221\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN USY4444LAA99\n" +
                "//FOO SOV/REGS/S.1 2.034 55/06\n" +
                "/25 USD\n" +
                ":90A::MRKT//PRCT/107,245000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/6000000,\n" +
                ":93B::AVAI//FAMT/6000000,\n" +
                ":19A::HOLD//USD6434700,\n" +
                ":19A::ACRU//USD30937,14\n" +
                ":70E::HOLD//20150805\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS1234567890\n" +
                "//JOE DOE 4.50000 67/08\n" +
                "/22 USD\n" +
                ":90A::MRKT//PRCT/100,004610\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/30000000,\n" +
                ":93B::AVAI//FAMT/30000000,\n" +
                ":19A::HOLD//USD30001383,\n" +
                ":19A::ACRU//USD463749,90\n" +
                ":70E::HOLD//20160817\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS0987654321\n" +
                "//FOO INSTITUTION 6.70000 UNDAT\n" +
                "ED  USD\n" +
                ":90A::MRKT//PRCT/98,000000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/20000000,\n" +
                ":93B::AVAI//FAMT/20000000,\n" +
                ":19A::HOLD//USD19600000,\n" +
                ":19A::ACRU//USD177222,20\n" +
                ":70E::HOLD//20180412\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS8463758477\n" +
                "//FOO TIER 1 BAR  9.80000 UNDAT\n" +
                "ED  USD\n" +
                ":90A::MRKT//PRCT/100,665000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/24405000,\n" +
                ":93B::AVAI//FAMT/24405000,\n" +
                ":19A::HOLD//USD24567293,25\n" +
                ":19A::ACRU//USD436239,38\n" +
                ":70E::HOLD//20170622\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3335566778\n" +
                "//FOOBAR ASSETS CORP 7.80000 09/11\n" +
                "/27 USD\n" +
                ":90A::MRKT//PRCT/94,135000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/37500000,\n" +
                ":93B::AVAI//FAMT/37500000,\n" +
                ":19A::HOLD//USD35300625,\n" +
                ":19A::ACRU//USD243750,\n" +
                ":70E::HOLD//20200323\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS9998877665\n" +
                "//ABC LIMITED   3.62800 20/04\n" +
                "/27 USD\n" +
                ":90A::MRKT//PRCT/109,350000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/50000000,\n" +
                ":93B::AVAI//FAMT/50000000,\n" +
                ":19A::HOLD//USD54675000,\n" +
                ":19A::ACRU//USD317450,\n" +
                ":70E::HOLD//20170421\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS1112233444\n" +
                "//FOOBAR LTD.    3.25100 23/05\n" +
                "/22 USD\n" +
                ":90A::MRKT//PRCT/101,980000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10198000,\n" +
                ":19A::ACRU//USD27091,60\n" +
                ":70E::HOLD//20170523\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333344444\n" +
                "//FOO REGS 4.397 01/06\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/98,180000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/20000000,\n" +
                ":93B::AVAI//FAMT/20000000,\n" +
                ":19A::HOLD//USD19636000,\n" +
                ":19A::ACRU//USD53741,\n" +
                ":70E::HOLD//20190131\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS5555544444\n" +
                "//ZZZ INTERNATIONAL 5.25000 20/03\n" +
                "/25 USD\n" +
                ":90A::MRKT//PRCT/104,230000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/5000000,\n" +
                ":93B::AVAI//FAMT/5000000,\n" +
                ":19A::HOLD//USD5211500,\n" +
                ":19A::ACRU//USD67812,50\n" +
                ":70E::HOLD//20170920\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS9999944444\n" +
                "//FOOBAR LTD       3.62500 06/02\n" +
                "/23 USD\n" +
                ":90A::MRKT//PRCT/103,165000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/50000000,\n" +
                ":93B::AVAI//FAMT/50000000,\n" +
                ":19A::HOLD//USD51582500,\n" +
                ":19A::ACRU//USD689756,50\n" +
                ":70E::HOLD//20180206\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS1234567890\n" +
                "//ABC LIMITED      4.50000 22/03\n" +
                "/28 USD\n" +
                ":90A::MRKT//PRCT/70,122758\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/6000000,\n" +
                ":93B::AVAI//FAMT/6000000,\n" +
                ":19A::HOLD//USD4207365,48\n" +
                ":19A::ACRU//USD51728,04\n" +
                ":70E::HOLD//20200604\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS8888888888\n" +
                "//ABCD SUKUK LTD       VAR     00/02\n" +
                "/23 USD\n" +
                ":90A::INDC//PRCT/98,455428\n" +
                ":94B::PRIC//THEO\n" +
                ":98A::PRIC//20181030\n" +
                ":93B::AGGR//FAMT/4400000,\n" +
                ":93B::AVAI//FAMT/4400000,\n" +
                ":19A::HOLD//USD4332038,83\n" +
                ":19A::ACRU//USD13461,76\n" +
                ":70E::HOLD//20180329\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS4443332222\n" +
                "//ABCDEF FOO 5.93200 31/10\n" +
                "/25 USD\n" +
                ":90A::MRKT//PRCT/103,140000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/29000000,\n" +
                ":93B::AVAI//FAMT/29000000,\n" +
                ":19A::HOLD//USD29910600,\n" +
                ":19A::ACRU//USD253263,38\n" +
                ":70E::HOLD//20181127\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS8888777766\n" +
                "//QUERTY 6.87500 05/10\n" +
                "/25 USD\n" +
                ":90A::MRKT//PRCT/111,255000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/70000000,\n" +
                ":93B::AVAI//FAMT/70000000,\n" +
                ":19A::HOLD//USD77878500,\n" +
                ":19A::ACRU//USD1042708,10\n" +
                ":70E::HOLD//20180416\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333344444\n" +
                "//FOO COMPANY 4.47100 24/04\n" +
                "/23 USD\n" +
                ":90A::MRKT//PRCT/105,405000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/15000000,\n" +
                ":93B::AVAI//FAMT/15000000,\n" +
                ":19A::HOLD//USD15810750,\n" +
                ":19A::ACRU//USD109912,05\n" +
                ":70E::HOLD//20180424\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS4444433333\n" +
                "//AAA COMPANY I 4.23100 18/04\n" +
                "/23 USD\n" +
                ":90A::MRKT//PRCT/104,470000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10447000,\n" +
                ":19A::ACRU//USD76393,\n" +
                ":70E::HOLD//20180418\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS4444433333\n" +
                "//FFF COMPANY L 4.37500 19/09\n" +
                "/23 USD\n" +
                ":90A::MRKT//PRCT/105,795000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10579500,\n" +
                ":19A::ACRU//USD114236,10\n" +
                ":70E::HOLD//20180919\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS2222233333\n" +
                "//FOO BAR G 4.72300 27/09\n" +
                "/28 USD\n" +
                ":90A::MRKT//PRCT/114,175000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/25000000,\n" +
                ":93B::AVAI//FAMT/25000000,\n" +
                ":19A::HOLD//USD28543750,\n" +
                ":19A::ACRU//USD282068,\n" +
                ":70E::HOLD//20200309\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS6666677777\n" +
                "//ABC LIMITED   4.30300 19/01\n" +
                "/29 USD\n" +
                ":90A::MRKT//PRCT/115,445000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/50000000,\n" +
                ":93B::AVAI//FAMT/50000000,\n" +
                ":19A::HOLD//USD57722500,\n" +
                ":19A::ACRU//USD920363,50\n" +
                ":70E::HOLD//20180919\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS2222233333\n" +
                "//AAABBB LIMITE 4.76000 05/12\n" +
                "/25 USD\n" +
                ":90A::MRKT//PRCT/109,190000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/5500000,\n" +
                ":93B::AVAI//FAMT/5500000,\n" +
                ":19A::HOLD//USD6005450,\n" +
                ":19A::ACRU//USD13090,\n" +
                ":70E::HOLD//20181205\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS4444499999\n" +
                "//SSS COMPANY L 3.87500 22/01\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/106,265000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/20000000,\n" +
                ":93B::AVAI//FAMT/20000000,\n" +
                ":19A::HOLD//USD21253000,\n" +
                ":19A::ACRU//USD325069,40\n" +
                ":70E::HOLD//20190122\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS4444455555\n" +
                "//JOE DOE HO 5.62500 27/02\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/103,620000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/36500000,\n" +
                ":93B::AVAI//FAMT/36500000,\n" +
                ":19A::HOLD//USD37821300,\n" +
                ":19A::ACRU//USD661562,50\n" +
                ":70E::HOLD//20200304\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS333332222211\n" +
                "//QQQQ FFFFF L 4.26400 05/03\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/105,150000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/15000000,\n" +
                ":93B::AVAI//FAMT/15000000,\n" +
                ":19A::HOLD//USD15772500,\n" +
                ":19A::ACRU//USD191880,\n" +
                ":70E::HOLD//20190305\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333344444\n" +
                "//FOBAR LTD.   3.98200 26/03\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/104,990000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10499000,\n" +
                ":19A::ACRU//USD96231,60\n" +
                ":70E::HOLD//20190326\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333322222\n" +
                "//ZZZ COMPANY L 3.89000 13/05\n" +
                "/29 USD\n" +
                ":90A::MRKT//PRCT/110,475000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD11047500,\n" +
                ":19A::ACRU//USD43222,20\n" +
                ":70E::HOLD//20190513\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS8888866666\n" +
                "//AAAA 1 DDDD LI 5.62500 UNDAT\n" +
                "ED  USD\n" +
                ":90A::MRKT//PRCT/96,665000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/14000000,\n" +
                ":93B::AVAI//FAMT/14000000,\n" +
                ":19A::HOLD//USD13533100,\n" +
                ":19A::ACRU//USD27971,30\n" +
                ":70E::HOLD//20190610\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS5555544444\n" +
                "//JJJJJYYYYY 2.98200 24/09\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/101,860000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10186000,\n" +
                ":19A::ACRU//USD73721,60\n" +
                ":70E::HOLD//20190924\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333399999\n" +
                "//ABC INTERNATIONAL S 4.50000 30/03\n" +
                "/27 USD\n" +
                ":90A::MRKT//PRCT/103,995000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/30000000,\n" +
                ":93B::AVAI//FAMT/30000000,\n" +
                ":19A::HOLD//USD31198500,\n" +
                ":19A::ACRU//USD311250,\n" +
                ":70E::HOLD//20190930\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333322222\n" +
                "//DDD EEEEE HO 4.10000 21/01\n" +
                "/27 USD\n" +
                ":90A::MRKT//PRCT/97,055000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/5000000,\n" +
                ":93B::AVAI//FAMT/5000000,\n" +
                ":19A::HOLD//USD4852750,\n" +
                ":19A::ACRU//USD86555,55\n" +
                ":70E::HOLD//20200121\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS8888877777\n" +
                "//QUERY LIMITED     25/02\n" +
                "/30 USD\n" +
                ":90A::MRKT//PRCT/100,585000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/50000000,\n" +
                ":93B::AVAI//FAMT/50000000,\n" +
                ":19A::HOLD//USD50292500,\n" +
                ":19A::ACRU//USD0,\n" +
                ":70E::HOLD//20200225\n" +
                ":16S:FIN\n" +
                ":16R:FIN\n" +
                ":35B:ISIN XS3333344444\n" +
                "//FOO BAR  6.25000 14/11\n" +
                "/24 USD\n" +
                ":90A::MRKT//PRCT/106,495000\n" +
                ":94B::PRIC//VEND\n" +
                ":98A::PRIC//20200619\n" +
                ":93B::AGGR//FAMT/10000000,\n" +
                ":93B::AVAI//FAMT/10000000,\n" +
                ":19A::HOLD//USD10649500,\n" +
                ":19A::ACRU//USD67708,30\n" +
                ":70E::HOLD//20200515\n" +
                ":16S:FIN\n" +
                ":16S:SUBSAFE\n" +
                "-}");
        repository.save(new MtSwiftMessage(mt535));
    }

}
