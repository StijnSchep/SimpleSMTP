package nl.avans;

import nl.avans.Domain.Mail;
import nl.avans.smtpstatemachine.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class SmtpContext extends Thread {
    //statemachine attributen
    private SmtpStateInf statemachine;
    private SmtpStateInf setNewState;

    //socket attributen
    private Socket socket = null;
    private PrintWriter out;

    //mail attributen
    private String hostname;
    private String mailFrom;
    private List<String> rcptTo = new ArrayList<String>();
    private StringBuilder body = new StringBuilder();

    public SmtpContext(Socket socket) {
        super("SmtpContextThread");
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        statemachine = new WelcomeState(this);
    }

    /*
        STATE MANIPULATION
        ==================
     */
    public void SetNewState(SmtpStateInf state){
        setNewState = state;
    }

    private void applyNewState(){
        if(setNewState!=null)
        {
            statemachine=setNewState;
            setNewState=null;
        }
    }

    /*
        THREADED INPUT PROCESSING
        =========================
     */
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("C: " + inputLine);
                statemachine.Handle(inputLine);
                if (inputLine.toUpperCase().equals("QUIT"))
                    break;
                applyNewState();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        SOCKETSTREAM MANIPULATION
        =========================
     */
    public void SendData(String data){
        out.println(data);
        System.out.println("S: " + data);
    }

    public void DisconnectSocket() {
        try {
            socket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void saveMail() {
        if(hostname == null)
            return;

        if(mailFrom == null)
            return;

        if(rcptTo.isEmpty())
            return;

        Server.savedMails.add(new Mail(mailFrom, rcptTo, body.toString()));
    }

    public void printMail(Mail mail) {
        out.print(mail);
        out.println();
    }

    /*
        GETTERS EN SETTERS
        ==================
     */
    public void SetHost(String hostname) {
        this.hostname = hostname;
    }

    public String GetHost() {
        return hostname;
    }

    public void SetMailFrom(String mailFrom) {
        this.mailFrom=mailFrom;
    }

    public String GetMailFrom() {
        return mailFrom;
    }

    public void AddRecipient(String recipient){
        rcptTo.add(recipient);
    }

    public void AddTextToBody(String text) {
        body.append(text);
        body.append("\r\n");
    }

}
