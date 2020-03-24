package nl.avans.smtpstatemachine;

import nl.avans.SmtpContext;

public class ReceivingDataState implements SmtpStateInf {
    private SmtpContext context;
    private boolean crReceived;
    private boolean dotReceived;

    ReceivingDataState(SmtpContext context) {
        this.context = context;
        context.SendData("354 Start mail input; end with <CRLF>.<CRLF>");
    }

    @Override
    public void Handle(String data) {
        if(data.matches("")) {
            System.out.println("Recieved CRLF");
            if(crReceived && dotReceived) {
                context.SendData("250 OK");
                context.saveMail();
                context.SetNewState(new WaitForMailFromState(context));
            } else {
                crReceived = true;
                dotReceived = false;
                context.AddTextToBody(data);
            }
            return;
        }

        if(data.matches(".")) {
            if(crReceived) {
                dotReceived = true;
            } else {
                context.AddTextToBody(data);
            }

            return;
        }

        crReceived = false;
        context.AddTextToBody(data);
    }
}
