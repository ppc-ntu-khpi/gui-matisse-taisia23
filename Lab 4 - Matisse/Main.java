import com.mybank.data.DataSource;
import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.reporting.CustomerReport;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class Main extends JFrame {
    private JPanel mainPanel;
    private JButton show;
    private JButton report;
    private JButton about;
    private JComboBox<String> clients;
    private JTextPane textPane;

    public Main() {
        setContentPane(mainPanel);
        setSize(500, 400);
        setTitle("MyBank clients");
        setVisible(true);

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer customer = Bank.getCustomer(clients.getSelectedIndex());
                String text;

                text = "<b>" + customer.getFirstName() + " " + customer.getLastName() + "</b>" + ", customer #" +
                    clients.getSelectedIndex() + "<br>" + "-".repeat(30) + "<br>";

                for (int i = 0; i < customer.getNumberOfAccounts(); i++) {
                    Account account = customer.getAccount(i);

                    String accountType = account instanceof CheckingAccount ? "Checking" : "Savings";

                    text += "#" + i + " - <b>" + accountType + "</b>: $" + account.getBalance() + "<br>";
                }

                textPane.setText(text);
            }
        });

        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(byteStream);

                System.setOut(printStream);

                new CustomerReport().generateReport();

                System.out.flush();
                System.setOut(System.out);

                textPane.setText(byteStream.toString().replace("\n", "<br>"));
            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textPane.setText("MyBank clients");
            }
        });

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getLastName() + " " + Bank.getCustomer(i).getFirstName());
        }
    }

    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Locale.setDefault(Locale.US);

        DataSource ds = new DataSource("./data/test.dat");
        ds.loadData();

        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        new Main();
    }
}
