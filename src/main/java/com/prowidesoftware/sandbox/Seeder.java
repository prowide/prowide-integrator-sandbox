package com.prowidesoftware.sandbox;

import com.prowidesoftware.swift.model.MtSwiftMessage;
import com.prowidesoftware.swift.model.field.Field20;
import com.prowidesoftware.swift.model.field.Field79;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103_STP;
import com.prowidesoftware.swift.model.mt.mt1xx.MT199;
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
        for (int i=0; i<5; i++) {
            MT199 mt = new MT199();
            mt.append(Field20.tag("MYREFERENCE" + i));
            mt.append(Field79.tag("Narrative" + i));
            repository.save(new MtSwiftMessage(mt.getSwiftMessage()));
        }

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
    }

}
