import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegisterForm extends JDialog{
    private JTextField tfEmail;
    private JTextField tfName;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPasswordField pfConfirmPass;
    private JPanel registerPanel;

    public RegisterForm(JFrame parent)
    {
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmpassword = String.valueOf(pfConfirmPass.getPassword());

        if(name.isEmpty()||email.isEmpty()||phone.isEmpty()||address.isEmpty()||password.isEmpty()||confirmpassword.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again",JOptionPane.ERROR_MESSAGE );
            return;
        }

        if(!password.equals(confirmpassword))
        {
            JOptionPane.showMessageDialog(this, "Confirm password doesn't match", "Try again",JOptionPane.ERROR_MESSAGE );
        }

        try {
            user = addUserToDatabase(name, email, phone, address, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(user!=null)
        {
            dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Failed to register new user!", "Try again",JOptionPane.ERROR_MESSAGE );
        }

    }

    public User user;
    private User addUserToDatabase(String name, String email, String phone, String address, String password) throws Exception
    {
        User user = null;

        Class.forName("org.sqlite.JDBC");

        Connection conn = DriverManager.getConnection("jdbc:sqlite:/Users/amrita/Documents/javaProjects/RegistrationForm/user.db");
        Statement stmt = conn.createStatement();
        String sql = "INSERT INTO users(name, email, phone, address, password)" + "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, phone);
        preparedStatement.setString(4, address);
        preparedStatement.setString(5, password);

        //INSERT rows into the table
        int addedRows= preparedStatement.executeUpdate();
        if(addedRows>0)
        {
            user = new User();
            user.name=name;
            user.email=email;
            user.phone=phone;
            user.address=address;
            user.password=password;
        }
        stmt.close();
        conn.close();
        return user;
    }

    public static void main(String[] args) {
        RegisterForm myForm = new RegisterForm(null);
        User user = myForm.user;
        if(user!=null)
            System.out.println("Successful registration of " + user.name + ".");
        else
            System.out.println("Registration cancelled.");
    }
}
