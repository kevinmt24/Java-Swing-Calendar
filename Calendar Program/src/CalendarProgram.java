import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class CalendarProgram{
    static JLabel lblMonth, lblYear, lbltitle,lblguests,lbllocation,lbldescription,lbldate;
    static JButton btnPrev, btnNext;
    static JTable tblCalendar;
    static JComboBox cmbYear;
    static JFrame frmMain;
    static Container pane;
    static DefaultTableModel mtblCalendar; 
    static JScrollPane stblCalendar; 
    static JPanel pnlCalendar,panel;
    static int realYear, realMonth, realDay, currentYear, currentMonth,count;
    
    static JButton addevent,hideevent,showevent;
    static JTextField txtdetails,txtdate,txtguests,txtlocation,txtdescription;
    
    static String detailsvar = null;
    static String a,b,c,d,f;
    
    public static void main (String args[]) throws Exception {
      
        createTable();	
    	try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}
        
        
        frmMain = new JFrame ("Calendar Application"); 
        frmMain.setSize(800,600); 
        pane = frmMain.getContentPane(); 							
        pane.setLayout(null); 										
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 	
        
        //Create controls
        lblMonth = new JLabel ("January 2020");
        lblYear = new JLabel ("Change year:");
        cmbYear = new JComboBox();
        btnPrev = new JButton ("Previous Month");
        btnNext = new JButton ("Next Month");
        mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tblCalendar = new JTable(mtblCalendar);
        stblCalendar = new JScrollPane(tblCalendar);
        pnlCalendar = new JPanel(null);
        addevent = new JButton("Add an Event");
        hideevent = new JButton("Hide an Event");
        showevent = new JButton("Show All Events this Month");
        
        //Set border
        pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        //Register action listeners
        btnPrev.addActionListener(new btnPrev_Action());
        btnNext.addActionListener(new btnNext_Action());
        cmbYear.addActionListener(new cmbYear_Action());
        addevent.addActionListener(new addevent_Action());
      //  hideevent.addActionListener(new hideevent_Action());
        showevent.addActionListener(new showevent_Action());
        
        //Add controls to pane
        pane.add(pnlCalendar);
        pnlCalendar.add(lblMonth);
        pnlCalendar.add(lblYear);
        pnlCalendar.add(cmbYear);
        pnlCalendar.add(btnPrev);
        pnlCalendar.add(btnNext);
        pnlCalendar.add(stblCalendar);
        pnlCalendar.add(addevent);
        pnlCalendar.add(hideevent);
        pnlCalendar.add(showevent);
        
        //Set bounds
        pnlCalendar.setBounds(30, 30, 750, 600);
        lblMonth.setBounds(30, 40, 150, 25);
        lblMonth.setFont(new Font("Verdana", Font.PLAIN, 20));
        lblYear.setBounds(525, 475, 100, 25);
        cmbYear.setBounds(600, 475, 100, 30);
        btnPrev.setBounds(50, 25, 150, 50);
        btnNext.setBounds(550, 25, 150, 50);
        stblCalendar.setBounds(30, 100, 645, 347);
        addevent.setBounds(30, 475, 100, 30);
        hideevent.setBounds(180, 475, 100, 30);
        showevent.setBounds(330,475,165,30);
        
        //Make frame visible
        frmMain.setResizable(false);
        frmMain.setVisible(true);
        
        //Get real month/year
        GregorianCalendar cal = new GregorianCalendar(); //Create calendar
        realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); //Get day
        realMonth = cal.get(GregorianCalendar.MONTH); //Get month
        realYear = cal.get(GregorianCalendar.YEAR); //Get year
        currentMonth = realMonth; //Match month and year
        currentYear = realYear;
        
        //Add headers
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; //All headers
        for (int i=0; i<7; i++){
            mtblCalendar.addColumn(headers[i]);
        }
        
        tblCalendar.getParent().setBackground(tblCalendar.getBackground()); //Set background
        
        //No resize/reorder
        tblCalendar.getTableHeader().setResizingAllowed(false);
        tblCalendar.getTableHeader().setReorderingAllowed(false);
        
        //Single cell selection
        tblCalendar.setColumnSelectionAllowed(true);
        tblCalendar.setRowSelectionAllowed(true);
        tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //Set row/column count
        tblCalendar.setRowHeight(53);
        mtblCalendar.setColumnCount(7);
        mtblCalendar.setRowCount(6);
        
        //Populate table
        for (int i=realYear-100; i<=realYear+100; i++){
            cmbYear.addItem(String.valueOf(i));
        }
        
        //Refresh calendar
        refreshCalendar (realMonth, realYear); //Refresh calendar
    }
    
    public static void refreshCalendar(int month, int year){
        //Variables
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som; //Number Of Days, Start Of Month
        
        //Allow/disallow buttons
        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);
        if (month == 0 && year <= realYear-10){btnPrev.setEnabled(false);} //Too early
        if (month == 11 && year >= realYear+100){btnNext.setEnabled(false);} //Too late
        lblMonth.setText(months[month]); //Refresh the month label (at the top)
        lblMonth.setBounds(350, 40, 150, 25); //Re-align label with calendar
        lblMonth.setFont(new Font("Verdana", Font.PLAIN, 20));
        cmbYear.setSelectedItem(String.valueOf(year)); //Select the correct year in the combo box
        
        //Clear table
        for (int i=0; i<6; i++){
            for (int j=0; j<7; j++){
                mtblCalendar.setValueAt(null, i, j);
            }
        }
        
        //Get first day of month and number of days
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);
        
        //Draw calendar
        for (int i=1; i<=nod; i++){
            int row = new Integer((i+som-2)/7);
            int column  =  (i+som-2)%7;
            mtblCalendar.setValueAt(i, row, column);
        }
        
        //Apply renderers
        tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
    }
    
    static class tblCalendarRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
        	super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (column == 0){ //Week-end
                setBackground(new Color(126,223,247));
            }
            else{ //Week
                setBackground(new Color(255, 255, 255));
            }
            if (value != null){
                if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){ //Today
                    setBackground(new Color(150, 240, 162));
                }
            }
            setBorder(null);
            setForeground(Color.black);
            return this;
        }
    }
    
    static class btnPrev_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0){ //Back one year
                currentMonth = 11;
                currentYear -= 1;
            }
            else{ //Back one month
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class btnNext_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){ //Foward one year
                currentMonth = 0;
                currentYear += 1;
            }
            else{ //Foward one month
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class cmbYear_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (cmbYear.getSelectedItem() != null){
                String b = cmbYear.getSelectedItem().toString();
                currentYear = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear);
            }
        }
    }
    
    static class addevent_Action implements ActionListener {
    	public void actionPerformed (ActionEvent e){
           
    		JFrame frame = new JFrame();
    		panel = new JPanel();
    		frame.setSize(500,500); 
    			
    	
    		frame.add(panel);	
    	    frame.setTitle("Event Manager");
    	    panel.setLayout(null);
    	    
    	    lbltitle = new JLabel("Add a Title : ");
    	    lbldate = new JLabel("Date : ");
    	    lblguests = new JLabel("Add Guests : ");
    	    lbllocation = new JLabel("Location : ");
    	    lbldescription = new JLabel("Description : ");
    	    
    	    txtdetails = new JTextField(30);
    	    txtdate = new JTextField(30);
    	    txtguests = new JTextField(30);
    	    txtlocation = new JTextField(30);
    	    txtdescription = new JTextField(30);
    	    
    	    JButton eventbutton = new JButton("Create Event");
    	    
    	    panel.add(lbltitle);
    	    panel.add(lbldate);
    	    panel.add(lblguests);
    	    panel.add(lbllocation);
    	    panel.add(lbldescription);
    	    
    	    panel.add(txtdetails);
    	    panel.add(txtdate);
    	    panel.add(txtguests);
    	    panel.add(txtlocation);
    	    panel.add(txtdescription);
    	    
    	    panel.add(eventbutton);
 
    	    lbltitle.setBounds(10, 50, 140,60);
    	    lbltitle.setFont(new Font("Verdana", Font.PLAIN, 17));
    	    lbldate.setBounds(10, 110, 140,60);
    	    lbldate.setFont(new Font("Verdana", Font.PLAIN, 17));
    	    lblguests.setBounds(10, 170, 140,60);
    	    lblguests.setFont(new Font("Verdana", Font.PLAIN, 17));
    	    lbllocation.setBounds(10, 230, 140,60);
    	    lbllocation.setFont(new Font("Verdana", Font.PLAIN, 17));
    	    lbldescription.setBounds(10, 290, 170,60);
    	    lbldescription.setFont(new Font("Verdana", Font.PLAIN, 17));
    	    
    	    txtdetails.setBounds(220,50,165,40);
    	    txtdate.setBounds(220,110,165,40);
    	    txtguests.setBounds(220,170,165,40);
    	    txtlocation.setBounds(220,230,165,40);
    	    txtdescription.setBounds(220,290,165,80);
    	   
    	    eventbutton.setBounds(180, 400, 120, 40);
    	    
    	    frame.setVisible(true);
    	    frame.setResizable(false);
    	    
    	    eventbutton.addActionListener(new eventbutton_Action());
    	    
    	}
    	
    
    
    }
    
    static class showevent_Action implements ActionListener {
    	public void actionPerformed (ActionEvent e){
           
    		JFrame showframe = new JFrame();
    		JPanel panel2 = new JPanel();
    		showframe.setSize(700,700); 
    			
    	
    		showframe.add(panel2);	
    	    showframe.setTitle("Event Manager");
    	    panel2.setLayout(null);
    	    showframe.setVisible(true);
    	    showframe.setResizable(false);
    	    
    	    JLabel upcominglbl = new JLabel("Upcoming Events : ");
    	    panel2.add(upcominglbl);
    	    upcominglbl.setBounds(10, 20, 200,60);
    	    upcominglbl.setFont(new Font("Verdana", Font.PLAIN, 20));
    	    
    	    try {
    	    	
    	    Connection conn1 = getConnection();
    	    PreparedStatement display = conn1.prepareStatement("SELECT details,date,guests,location,description FROM eventtable where date >= CURDATE() order by date;");
    	    int col_displacement = 70;
    	    count = 1;
    	    ResultSet result = display.executeQuery();
    	    while(result.next()) {
    	    	
    	    	a = result.getString("details");
    	    	b = result.getString("date");
    	    	c = result.getString("guests");
    	    	d = result.getString("location");
    	    	f = result.getString("description");
    	    	
    	    	JLabel dispdetail = new JLabel(count +".   "+ result.getString("details"));
    	    	JLabel dispdate = new JLabel("Due On "+result.getString("date"));
    	    	JButton dispall = new JButton("Show more");
    	 
    	    	panel2.add(dispdetail);
    	    	panel2.add(dispdate);
    	    	panel2.add(dispall);
    	  
    	    	dispdetail.setBounds(40, col_displacement, 250,30);
    	    	dispdetail.setFont(new Font("Verdana", Font.PLAIN, 16));
    	    	dispdate.setBounds(270, col_displacement, 250,30);
    	    	dispdate.setFont(new Font("Verdana", Font.PLAIN, 16));
    	    	dispall.setBounds(450, col_displacement,120, 30);
    	    	
    	    	dispall.addActionListener(new dispall_Action());
    	    	
    	    	col_displacement+=40;
    	    	count++;
    	    }
    	    
    	    }catch(Exception e1) {System.out.println(e1);}
    	    
    	    
    	    
   }
    }
    
    static class dispall_Action implements ActionListener {
    	public void actionPerformed (ActionEvent e) { 
    		JFrame dispframe = new JFrame();
    		JPanel panel3 = new JPanel();
    		dispframe.setSize(500,500); 
    			
    	
    		dispframe.add(panel3);	
    	    dispframe.setTitle("Event Highlights");
    	    panel3.setLayout(null);
    	    dispframe.setVisible(true);
    	    dispframe.setResizable(false);
    	    
    	    JLabel a1 = new JLabel(a);
    	    JLabel b1 = new JLabel(b);
    	    JLabel c1 = new JLabel(c);
    	    JLabel d1 = new JLabel(d);
    	    JLabel f1 = new JLabel(f);
    	    
    	    
    	    JLabel a2 = new JLabel("Details : ");
    	    JLabel b2 = new JLabel(" Time : ");
    	    JLabel c2 = new JLabel("Guests : ");
    	    JLabel d2 = new JLabel("Location : ");
    	    JLabel f2 = new JLabel("Description : ");
    	    panel3.add(a1);
    	    panel3.add(b1);
    	    panel3.add(c1);
    	    panel3.add(d1);
    	    panel3.add(f1);
    	    panel3.add(a2);
    	    panel3.add(b2);
    	    panel3.add(c2);
    	    panel3.add(d2);
    	    panel3.add(f2);
    	    
    	    
    	    a1.setBounds(250,20 , 250,30);
	    	a1.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	b1.setBounds(250,60 , 250,30);
	    	b1.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	c1.setBounds(250,100 , 250,30);
	    	c1.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	d1.setBounds(250,140 , 250,30);
	    	d1.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	f1.setBounds(250,180 , 250,30);
	    	f1.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	
	    	a2.setBounds(20,20 , 250,30);
	    	a2.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	b2.setBounds(20,60 , 250,30);
	    	b2.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	c2.setBounds(20,100 , 250,30);
	    	c2.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	d2.setBounds(20,140 , 250,30);
	    	d2.setFont(new Font("Verdana", Font.PLAIN, 16));
	    	f2.setBounds(20,180 , 250,30);
	    	f2.setFont(new Font("Verdana", Font.PLAIN, 16));
    	    
    	    
    	    
    	   
    	} }
 
    static class eventbutton_Action implements ActionListener {
    	public void actionPerformed (ActionEvent e) {
    		
    	String datevar = null;
 
    	detailsvar = txtdetails.getText();
    	datevar = txtdate.getText();
    	String guestsvar = txtguests.getText();	
    	String locationvar = txtlocation.getText();	
    	String descriptionvar = txtdescription.getText();
    	
    	if(detailsvar == null || datevar == null)
    	{
    		JLabel lbleventcreate = new JLabel(" Title and Date details Mandatory ! ");
    		panel.add(lbleventcreate);
    		lbleventcreate.setBounds(320, 390, 140,60);
    	}
    	else
    	{
    		JLabel lbleventcreate = new JLabel(" Event has Been created ! ");
    		 panel.add(lbleventcreate);
    		 lbleventcreate.setBounds(320, 390, 140,60);
    		try {
				insertTable(detailsvar,datevar,guestsvar,locationvar,descriptionvar);
			} catch (Exception e1) {System.out.println(e1);}
    	}
    }
    }
    
    public static Connection getConnection() throws Exception{
    	
    	try {
    		
    		String driver = "com.mysql.jdbc.Driver";
    		String url = "jdbc:mysql://localhost:3306/calendardb";
    		String username = "root";
    		String password = "1@eddardstark";
    		Class.forName(driver);
    		
    		Connection conn = DriverManager.getConnection(url,username,password);
    		System.out.println("Connected");
    		return conn;
    	}catch(Exception e) {System.out.println(e);}
    	
    	
    	return null;
    }
    
    public static void createTable() throws Exception {
    	try {
    		Connection conn = getConnection();
    		PreparedStatement create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS`calendardb`.`eventtable` (`id` INT NOT NULL AUTO_INCREMENT,`details` VARCHAR(80) NOT NULL,`date` DATE NOT NULL,`guests` VARCHAR(200) NULL,`location` VARCHAR(200) NULL,`description` VARCHAR(200) NULL, PRIMARY KEY (`id`)) ;");
    		create.executeUpdate();
    	
    	}catch(Exception e) {System.out.println(e);}
    	
    	finally{System.out.println("Table Created");}
    	
    	
    }
    
    public static void insertTable(String var1,String var2,String var3,String var4,String var5) throws Exception {
    	try {
    		Connection conn = getConnection();
    		PreparedStatement insert = conn.prepareStatement("INSERT INTO eventtable (details,date,guests,location,description) VALUES ('"+var1+"','"+var2+"','"+var3+"','"+var4+"','"+var5+"') ");     
    		insert.executeUpdate();
    				
    	}catch(Exception e) {System.out.println("Error");}
    
    	finally{System.out.println("Values Inserted ");}
    }
    
    
    
}