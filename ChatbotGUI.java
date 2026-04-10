import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.util.*;

public class ChatbotGUI {

    static JPanel chatPanel;
    static JScrollPane scrollPane;
    static JTextField inputField;

    static Map<String,String> memory = new HashMap<>();
    static int messageCount = 0;
    static String lastIntent = "";

    public static void main(String[] args){

        JFrame frame = new JFrame("Advanced AI Chatbot");
        frame.setSize(450,650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Color bg = new Color(10,25,47);
        Color userColor = new Color(31,111,235);
        Color botColor = new Color(40,60,100);

        JLabel header = new JLabel("🤖 Advanced Chatbot",JLabel.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(17,34,64));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial",Font.BOLD,18));
        header.setPreferredSize(new Dimension(100,50));
        frame.add(header,BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatPanel.setBackground(bg);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        frame.add(scrollPane,BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(17,34,64));

        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");

        sendBtn.setBackground(userColor);
        sendBtn.setForeground(Color.WHITE);

        inputPanel.add(inputField,BorderLayout.CENTER);
        inputPanel.add(sendBtn,BorderLayout.EAST);

        frame.add(inputPanel,BorderLayout.SOUTH);

        addMessage("Bot","Hello! I'm your advanced AI 🤖",botColor,false);

        sendBtn.addActionListener(e->sendMessage(userColor,botColor));
        inputField.addActionListener(e->sendMessage(userColor,botColor));

        frame.setVisible(true);
    }

    static void sendMessage(Color userColor, Color botColor){

        String text = inputField.getText().trim();
        if(text.isEmpty()) return;

        messageCount++;

        addMessage("You",text,userColor,true);
        inputField.setText("");

        String response = getResponse(text);
        addMessage("Bot",response,botColor,false);

        saveChat(text,response);
    }

    // ✅ UPDATED METHOD (SIZE FIX ONLY)
    static void addMessage(String sender,String message,Color color,boolean isUser){

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(10,25,47));

        int maxWidth = 260;

        JLabel msg = new JLabel("<html><div style='width:"+maxWidth+"px'>" + message + "</div></html>");
        msg.setOpaque(true);
        msg.setBackground(color);
        msg.setForeground(Color.WHITE);
        msg.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBackground(new Color(10,25,47));

        if(isUser){
            bubble.add(msg,BorderLayout.EAST);
        } else {
            bubble.add(msg,BorderLayout.WEST);
        }

        wrapper.add(bubble,BorderLayout.CENTER);

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(8));

        chatPanel.revalidate();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    static String getResponse(String input){

        String text = input.toLowerCase();

        if(text.equals("/clear")){
            chatPanel.removeAll();
            chatPanel.revalidate();
            chatPanel.repaint();
            return "Chat cleared!";
        }

        if(text.equals("/stats")){
            return "Total messages: " + messageCount;
        }

        if(text.equals("/help")){
            return "/clear /help /time /stats\nTry chatting normally too 😊";
        }

        if(text.equals("/time")){
            return "Time: " + java.time.LocalTime.now().withNano(0);
        }

        if(text.contains("my name is")){
            String name = input.substring(input.toLowerCase().indexOf("my name is")+11).trim();
            memory.put("name",name);
            return "Nice to meet you " + name;
        }

        if(text.contains("who am i")){
            return memory.containsKey("name") ? "You are " + memory.get("name") : "I don't know yet 😅";
        }

        if(lastIntent.equals("ask_stock")){
            lastIntent="";
            return "Great choice! That stock is popular 📈";
        }

        if(text.contains("stock")){
            lastIntent="ask_stock";
            return "Which stock are you interested in?";
        }

        if(text.matches(".*\\b(hi|hello|hey)\\b.*")){
            return memory.containsKey("name") ? "Hello "+memory.get("name")+" 😊" : "Hello 😊";
        }

        if(text.contains("how are you")){
            return "I'm doing great 💙";
        }

        if(text.contains("date")){
            return "Date: " + java.time.LocalDate.now();
        }

        if(text.contains("bye")){
            return "Goodbye 👋";
        }

        String[] replies = {
            "Interesting 🤔",
            "Tell me more 👀",
            "I understand 😊",
            "Nice 👍",
            "I'm learning more every day 🤖"
        };

        return replies[new Random().nextInt(replies.length)];
    }

    static void saveChat(String user, String bot){
        try{
            FileWriter fw = new FileWriter("chat.txt",true);
            fw.write("User: "+user+"\nBot: "+bot+"\n\n");
            fw.close();
        }catch(Exception e){}
    }
}