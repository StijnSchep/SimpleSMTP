package nl.avans.smtpstatemachine;

import nl.avans.Domain.Mail;
import nl.avans.Server;
import nl.avans.SmtpContext;

public class WaitForMailFromState implements SmtpStateInf {
    private SmtpContext context;

    WaitForMailFromState(SmtpContext context) {
        this.context=context;
    }

    @Override
    public void Handle(String data) {
        if(data.toUpperCase().startsWith("MAIL FROM:")) {
            String mailFrom = data.substring(10).trim();

            // Check if address matches form <user@domain.nl> or <user@sub.domain.nl>
            if(mailFrom.matches("[<][A-z][A-z]*[@][A-z][A-z]*[.]?[A-z][A-z]*[.][A-z][A-z]*[>]")) {
                context.SetMailFrom(mailFrom);
                context.SendData("250 OK");
                context.SetNewState(new WaitForRcptToState(context));
            } else {
                context.SendData("550 Invalid 'Mail From' Argument");
            }

            return;
        }

        if(data.toUpperCase().startsWith("PRINT MAILS")) {
            for(Mail m : Server.savedMails) {
                context.printMail(m);
            }

            return;
        }

        if(data.toUpperCase().startsWith("QUIT")) {
            context.SendData("221 Bye");
            context.DisconnectSocket();
            return;
        }
        context.SendData("503 Invalid Input");
    }
}
