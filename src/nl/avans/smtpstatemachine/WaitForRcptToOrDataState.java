package nl.avans.smtpstatemachine;

import nl.avans.SmtpContext;

public class WaitForRcptToOrDataState implements SmtpStateInf {
    private SmtpContext context;

    WaitForRcptToOrDataState(SmtpContext context) {
        this.context = context;
    }

    @Override
    public void Handle(String data) {
        if(data.toUpperCase().startsWith("RCPT TO:")) {
            String rcptTo = data.substring(8).trim();

            // Check if address matches form <user@domain.nl> or <user@sub.domain.nl>
            if(rcptTo.matches("[<][A-z][A-z]*[@][A-z][A-z]*[.]?[A-z][A-z]*[.][A-z][A-z]*[>]")) {
                context.AddRecipient(rcptTo);
                context.SendData("250 OK");
            } else {
                context.SendData("550 Invalid 'RCPT TO' Argument");
            }

            return;
        }

        if(data.toUpperCase().startsWith("DATA")) {
            context.SetNewState(new ReceivingDataState(context));
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
