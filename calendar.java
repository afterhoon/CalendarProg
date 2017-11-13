/*
 * 포스트잇을 달력쪽에 가져다 놓으면 패널 크기 작아짐
 * (원래 서서히 작아지게 Thread.sleep으로 했는데 딜레이만 먹고 애니메이션 안나옴
 * 
 * 포스트잇을 달력이 아닌곳에 두면 다시 제자리로 돌아감
 * 
 * 
 * 휘진 수정)
 * 애니메이션 구현
 * 달력 범위 계산식 수정
 * 포스트잇 끌어다가 달력 위에 놓으면 줄어들기+반투명, 달력 밖이면 커지기+불투명 -> 이 과정에서 전역변수 bigMode 선언
 * 
 * 
 * */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.border.*;

public class calendar extends JFrame{

	///////// TEST ///////
	JLabel testLa = new JLabel("(x,y)");
	/////////////////////

	JPanel contentPane = new JPanel();
	CalendarPanel calPan = new CalendarPanel();
	AddPanel addPan = new AddPanel();
	InfoPanel infoPan = new InfoPanel();
	//	Schedule sd[] = new Schedule[100];  // 스케줄의 카운트 = sdCnt;
	Vector<Schedule> vSd = new Vector<Schedule>(100);
	Schedule targetSchedule = null;

	final int frameWidth = 1200;
	final int frameHeight = 800;

	// InfoPanel의 (x, y, width, height)
	int ipx = 750;
	int ipy = 35;
	final int ipWidth = 400;
	final int ipHeight = 300;

	// CalendarPanel의 (x, y, width, height)
	int cpx = 25;
	int cpy = 25;
	final int cpWidth = 700;
	final int cpHeight = 700;

	// 각각 AddPanel의 (x, y, width, height)
	final int apdfx = 750;
	final int apdfy = 400;
	int apx = 750;
	int apy = 400;
	final int apWidth = 400;
	final int apHeight = 300;
	int apMoveWidth = 100;
	int apMoveHeight = 50;
	boolean bigMode = true; // addPan이 큰 상태면 true, 작은 상태면 false

	// 스케줄의 카운트
	int sdCnt = 0;

	public calendar() {
		setTitle("Calendar Frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		makeGUI();

		setVisible(true);
		setSize(frameWidth, frameHeight);
	}

	private void makeGUI() {
		contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(null);

		addComponent(contentPane, testLa, 600, 5, 200, 25);
		addComponent(contentPane, addPan, apx, apy, apWidth, apHeight);
		addComponent(contentPane, infoPan, ipx, ipy, ipWidth, ipHeight);
		addComponent(contentPane, calPan, cpx, cpy, cpWidth, cpHeight);

		addMouseMotionListener(new MouseMotionAdapter() {
			Point start;
			public void mouseMoved(MouseEvent e) {
				start = e.getPoint();
				testLa.setText("(" + start.x + "," + start.y + ")");
			}
			public void mouseDragged(MouseEvent e) {
				Point end = e.getPoint();
				testLa.setText("(" + start.x + "," + start.y + ")" + "  -->  " + "(" + end.x + "," + end.y + ")");
			}
		});

	}

	private class Schedule {
		private int year = 0;
		private int month = 0;
		private int day = 0;
		private int hour = 0;
		private String content = "";

		Schedule() {

		}

		Schedule(int y, int m, int d, int h, String s) {
			year = y;
			month = m;
			day = d;
			hour = h;
			content = s;
		}

		Schedule(Schedule sd) {
			year = sd.getYear();
			month = sd.getMonth();
			day = sd.getDay();
			hour = sd.getHour();
			content = sd.getContent();
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getDay() {
			return day;
		}

		public int getHour() {
			return hour;
		}

		public String getContent() {
			return content;
		}

		public void setYear(int y) {
			year = y;
		}

		public void setMonth(int m) {
			month = m;
		}

		public void setDay(int d) {
			day = d;
		}

		public void setHour(int h) {
			hour = h;
		}

		public void setContent(String s) {
			content = s;
		}
		public String toString () {
			String str = year + "." + month + "." + day + "/ " + hour + "시/ " + content;
			return str;
		}

	}

	private class InfoPanel extends JPanel {
		JList list = new JList(vSd);
		JPanel contentPane = new JPanel();
		public InfoPanel() {
			setBorder(BorderFactory.createTitledBorder("Schedule"));
			setLayout(new BorderLayout());
			setContentPane(contentPane);
			setBackground(new Color(95, 216, 250));

			add(new ScrollPane().add(list)).setBackground(new Color(151, 234, 244));
//			add(list);

			/*
			 * list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					Schedule temp = (Schedule) e.getSource();
					addPan.timeTf.setText(Integer.toString(temp.getHour()));
					addPan.contTa.setText(temp.getContent());
					targetSchedule = temp;
				}
			});
			 * */

			vSd.addElement(new Schedule(2017, 11 , 5, 10, "윈프 과제"));

			list.updateUI();
			//        addComponent(contentPane, list, 1100, ipHeight/2 - 10/2,100,10);
		}

	}

	private class CalendarPanel extends JPanel implements ActionListener, ChangeListener {

		JPanel frame;    //메인 프레임
		JPanel calendarPanel;  //달력 전체 패널
		JPanel changePanel;    //JSpinner, JComboBox 담고 있는 패널
		JPanel datePanel;    //날짜 부분 패널

		JButton lYearBut;
		JButton lMonBut;
		JButton nMonBut;
		JButton nYearBut;

		JCalendar jcalendar;  //JCalendar 객체
		//      ArrayList<JLabel> dateList;  //날짜JLabel를 담을 리스트
		ArrayList<JButton> dateList;

		JSpinner changeYear;
		JComboBox changeMonth;
		JLabel yearLa;
		JLabel monthLa;

		public CalendarPanel() {
			setOpaque(true);
			setBackground(Color.BLUE);
			setBounds(300, 400, 500, 700);
			setLayout(new BorderLayout());


			frame = new JPanel();
			calendarPanel = new JPanel();
			changePanel = new JPanel();
			datePanel = new JPanel();
			
			changePanel.setLayout(new GridLayout(1,6));

			jcalendar = new JCalendar();
			//        dateList = new ArrayList<JLabel>();
			dateList = new ArrayList<JButton>();

			calendarPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Calendar"));
			calendarPanel.setLayout(new BorderLayout());

			//        SpinnerModel yearModel = new SpinnerNumberModel(jcalendar.getYear(), jcalendar.getYear()-100, jcalendar.getYear()+100, 1);
			//        changeYear = new JSpinner(yearModel);
			//        changeYear.setEditor(new JSpinner.NumberEditor(changeYear, "#"));  

			String[] month = new String[12];
			for (int i = 0; i < month.length; i++) {
				month[i] = i + 1 + "월";
			}
			//        changeMonth = new JComboBox(month);
			//        changeMonth.setSelectedIndex(jcalendar.getMonth());

			//        changeYear.addChangeListener(this);
			//        changeMonth.addActionListener(this);

			lYearBut = new JButton("<<");
			changePanel.add(lYearBut);
			lMonBut = new JButton("<");
			changePanel.add(lMonBut);

			yearLa = new JLabel(Integer.toString(jcalendar.getYear()));
			changePanel.add(yearLa);
			yearLa.setHorizontalAlignment(SwingConstants.CENTER);
			monthLa = new JLabel(Integer.toString(jcalendar.getMonth()));
			changePanel.add(monthLa);
			monthLa.setHorizontalAlignment(SwingConstants.CENTER);
			
			
			//        changeMonth.setPreferredSize(new Dimension(60, 22));
			//        changePanel.add(changeYear);
			//        changePanel.add(changeMonth);

			nMonBut = new JButton(">");
			changePanel.add(nMonBut);
			nYearBut = new JButton(">>");
			changePanel.add(nYearBut);

			lYearBut.addActionListener(new BtnListener());
			lMonBut.addActionListener(new BtnListener());
			nMonBut.addActionListener(new BtnListener());
			nYearBut.addActionListener(new BtnListener());

			changePanel.setPreferredSize(new Dimension(250, 40));
			calendarPanel.add(BorderLayout.NORTH, changePanel);


			datePanel.setLayout(new GridLayout(7, 7));  
			String[] dayOfWeekName = {"일", "월", "화", "수", "목", "금", "토"};  
			for (int i = 0; i < dayOfWeekName.length; i++) {
				datePanel.add(addJLabel(dayOfWeekName[i], Color.WHITE));
			}  

			if (jcalendar.getFirstdayOfWeek() == 0) {
				jcalendar.setFirstdayOfWeek(7);
			}

			for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
				JButton button = addJButton("", null);
				dateList.add(button);
				datePanel.add(button);
				button.setEnabled(false);
			}

			for (int i = 0; i < jcalendar.getLastday(); i++) {
				JButton button = addJButton(i + 1 + "", null);
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JButton b = (JButton)e.getSource();
						System.out.println("----------------------------------------------------");
						for(int j = 0 ; j < vSd.size() ; j++) {
							if(isSameDate(jcalendar.getYear(), jcalendar.getMonth(), Integer.parseInt(b.getText()), 
									vSd.elementAt(j).getYear(), vSd.elementAt(j).getMonth(), vSd.elementAt(j).getDay())) {
								String str = vSd.elementAt(j).getHour() + "시/ " + vSd.elementAt(j).getContent();
								//								infoPan.vListData.add(str);
								System.out.println(str);
							}
						}
						System.out.println("----------------------------------------------------");
						for(int j = 0 ; j < vSd.size(); j++) {
							String str = vSd.elementAt(j).getHour() + "시/ " + vSd.elementAt(j).getContent();
							System.out.println(str);
						}
						System.out.println("----------------------------------------------------");


						/*
                   if(Integer.parseInt(b.getText()) == sd[j].getDay())
                       if(jcalendar.getMonth() == sd[j].getMonth())
                         if(jcalendar.getYear() == sd[j].getYear()) {
                             String str = sd[j].getHour() + "시/ " + sd[j].getContent();
                             infoPan.vListData.add(str);
                             System.out.println(str);
                         }
						 */
					}
				});
				dateList.add(button);
				datePanel.add(button); 
				button.setEnabled(true);
			}

			int afterEmpty = 42 - jcalendar.getLastday() - jcalendar.getFirstdayOfWeek();
			for (int i = 0; i < afterEmpty; i++) {
				JButton button = addJButton("", null);
				dateList.add(button);
				datePanel.add(button);
				button.setEnabled(false);
			}

			/*
         for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
           JLabel label = addJLabel("", null);
           dateList.add(label);
           datePanel.add(label);
       }

       for (int i = 0; i < jcalendar.getLastday(); i++) {
           JLabel label = addJLabel(i + 1 + "", null);
           dateList.add(label);
           datePanel.add(label); 
       }

       int afterEmpty = 42 - jcalendar.getLastday() - jcalendar.getFirstdayOfWeek();
       for (int i = 0; i < afterEmpty; i++) {
           JLabel label = addJLabel("", null);
           dateList.add(label);
           datePanel.add(label);
       }
			 * */

			setWeekend();

			calendarPanel.add(BorderLayout.CENTER, datePanel);

			add(BorderLayout.CENTER, calendarPanel);

		}

		public JButton addJButton(String text, Color color) {
			JButton button = new JButton();
			button.setText(text);
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setBorder(new EtchedBorder());
			if (color != null) {
				button.setOpaque(true);
				button.setBackground(color);
			}
			return button;
		}

		public JLabel addJLabel(String text, Color color) {
			JLabel label = new JLabel();
			label.setText(text);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBorder(new EtchedBorder());
			if (color != null) {
				label.setOpaque(true);
				label.setBackground(color);
			}
			return label;
		}

		private class BtnListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();

				int year = jcalendar.getYear();
				int month = jcalendar.getMonth();

				System.out.println(b.getText());

				switch(b.getText()) {
				case "<<": year--; break;
				case "<": month--; break;
				case ">": month++; break;
				case ">>": year++; break;
				}

				if(month == 13) {
					month = 1;
					year++;
				}
				if(month == 0) {
					month = 12;
					year--;
				}

				jcalendar.setCalendar(year, month, 1);
				yearLa.setText(Integer.toString(jcalendar.getYear()));
				monthLa.setText(Integer.toString(jcalendar.getMonth()));

				resetDateList();
			}
		}

		public void actionPerformed(ActionEvent e) {

			JComboBox obj = (JComboBox)e.getSource();
			int month = obj.getSelectedIndex();
			jcalendar.setCalendar(jcalendar.getYear(), month, 1);
			resetDateList();
		}

		public void stateChanged(ChangeEvent e) {

			JSpinner obj = (JSpinner)e.getSource();
			Integer y = (Integer)obj.getValue();
			int year = y.intValue();
			jcalendar.setCalendar(year, jcalendar.getMonth(), 1);
			resetDateList();
		}

		public void resetDateList() {

			if (jcalendar.getFirstdayOfWeek() == 0) {
				jcalendar.setFirstdayOfWeek(7);
			}

			for (int i = 0; i < jcalendar.getFirstdayOfWeek(); i++) {
				dateList.get(i).setText("");
				dateList.get(i).setEnabled(false);
			}
			for (int i = 0; i < jcalendar.getLastday(); i++) {
				dateList.get(jcalendar.getFirstdayOfWeek() + i).setText(i + 1 + "");
				dateList.get(jcalendar.getFirstdayOfWeek() + i).setEnabled(true);
			}
			int afterEmpty = jcalendar.getFirstdayOfWeek() + jcalendar.getLastday();
			int last = dateList.size() - afterEmpty;
			for (int i = 0; i < last; i++) {
				dateList.get(afterEmpty + i).setText("");
				dateList.get(afterEmpty + i).setEnabled(false);
			}

			setWeekend();
			datePanel.updateUI();
		}

		public void setWeekend() {

			for (int i = 0; i < dateList.size(); i++) {

				if (i % 7 == 0) {
					dateList.get(i).setForeground(Color.RED);
				}
				else if (i % 7 == 6) {
					dateList.get(i).setForeground(Color.blue);
				}
			}

		}

		public class JCalendar {

			private Calendar cal;
			private int year;    //연
			private int month;    //월
			private int today;    //오늘
			private int firstdayOfWeek;  //1일의 요일
			private int lastday;  //한달의 최대 날짜

			public JCalendar() {

				cal = Calendar.getInstance();
				year = cal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH);
				today = cal.get(Calendar.DAY_OF_MONTH);

				setCalendar(year, month, 1);
			}

			public void setCalendar(int year, int month, int date) {

				cal.set(year, month, date);
				this.year = year;
				this.month = month;
				firstdayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
				lastday = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			}

			public void setFirstdayOfWeek(int firstdayOfWeek) {
				this.firstdayOfWeek = firstdayOfWeek;
			}

			public Calendar getCal() {
				return cal;
			}

			public int getFirstdayOfWeek() {
				return firstdayOfWeek;
			}

			public int getLastday() {
				return lastday;
			}

			public int getMonth() {
				return month;
			}

			public int getToday() {
				return today;
			}

			public int getYear() {
				return year;
			}

		}
	}

	private class AddPanel extends JPanel {
		JLabel timeLa = new JLabel("시간");
		JLabel contLa = new JLabel("내용");
		JTextField timeTf = new JTextField(30);
		JTextArea contTa = new JTextArea(5, 30);

		int hour;
		String cont;

		private class Anim_Shrink extends Thread{
			public void run(){
				if(bigMode){
					int w = addPan.getWidth();
					int h = addPan.getHeight();

					timeLa.setVisible(false);
					contLa.setVisible(false);
					timeTf.setVisible(false);
					contTa.setVisible(false);

					for(int i = 0 ; i <= 101 ; i++) {
						try {
							sleep(3);
						} catch(InterruptedException e1) {

						}
						addPan.setSize(w - 3 * i, h - 2 * i);
						addPan.setLocation(new Point(addPan.getX() + i%2 + 1, addPan.getY() + 1));
						addPan.updateUI();
					}
					// 아래 두 문장 : 정확히 한 칸에 맞게 하기 위해
					addPan.setSize(addPan.getWidth(), addPan.getHeight() - 8);
					int x = ((addPan.getX() - 40) / 98 + 1) * 98 + 40 - 8;
					int y = ((addPan.getY() - 120) / 90 + 1) * 90 + 120 - 36;
					addPan.setLocation(new Point(x, y + 4));
					System.out.println(x + "  " + y);

					addPan.updateUI();
					try {
						sleep(100);
					} catch (InterruptedException e) {

					}
					apx = apdfx;
					apy = apdfy;
					addPan.setSize(apWidth, apHeight);
					addPan.setLocation(apx, apy);
					setTransparent(addPan, 255);

					timeLa.setVisible(true);
					contLa.setVisible(true);
					timeTf.setVisible(true);
					contTa.setVisible(true);

					bigMode = true;
				}
			}
		}

		private class Anim_Expand extends Thread{
			public void run(){
				if(!bigMode){
					int w = addPan.getWidth();
					int h = addPan.getHeight();

					for(int i = 0 ; i <= 101 ; i++) {
						try {
							sleep(5);
						} catch(InterruptedException e1) {

						}
						addPan.setSize(w + 3 * i, h + 2 * i);
						addPan.setLocation(new Point(addPan.getX() - i%2 - 1, addPan.getY() - 1));
						addPan.updateUI();
					}
					addPan.setSize(addPan.getWidth(), addPan.getHeight() + 8); // 사이즈 원상복구

					addPan.updateUI();
					bigMode = true;
				}

				addPan.setLocation(apdfx, apdfy);
			}
		}

		private AddPanel() {
			Schedule temp = new Schedule();
			setBorder(BorderFactory.createTitledBorder("Memo"));
			setOpaque(true);
			setBackground(new Color(216, 95, 250));

			setLayout(new FlowLayout());

			add(timeLa).setBackground(new Color(234, 151, 244));
			add(timeTf).setBackground(new Color(234, 151, 244));
			add(contLa).setBackground(new Color(234, 151, 244));
			add(contTa).setBackground(new Color(234, 151, 244));

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
						hour = Integer.parseInt(timeTf.getText());
					} catch(NumberFormatException e1) {
						hour = 0;
					}
					cont = contTa.getText();
					// 투명도 설정
					setTransparent(addPan, 100);

					if(timeTf.getText() != "" && contTa.getText() != "") {
						temp.setHour(hour);
						temp.setContent(cont);
						timeTf.setText("");
						contTa.setText("");
					}
				}

				public void mouseReleased(MouseEvent e) {
					if((apx+addPan.getWidth()/2 >= cpx && apx+addPan.getWidth()/2 <= cpx + cpWidth)
							&& (apy+addPan.getHeight()/2 >= cpy && apy+addPan.getHeight()/2 <= cpy + cpHeight)) {
						//                  sd[sdCnt] = new Schedule(calPan.jcalendar.getYear(), calPan.jcalendar.getMonth(), 1, temp.getHour(), temp.getContent());
						temp.setYear(calPan.jcalendar.getYear());
						temp.setMonth(calPan.jcalendar.getMonth());
						temp.setDay(1);
						vSd.addElement(new Schedule(temp));
						String str = vSd.elementAt(vSd.size() - 1).getYear() + "." + vSd.elementAt(vSd.size() - 1).getMonth() + "." + vSd.elementAt(vSd.size() - 1).getDay() + "/" + vSd.elementAt(vSd.size() - 1).getHour() + "/" + vSd.elementAt(vSd.size() - 1).getContent();
						System.out.println("This: " + str);
						//						infoPan.vListData.addElement(str);
						infoPan.list.updateUI();
						sdCnt++;

						// 사이즈 작아지는 애니메이션
						new Anim_Shrink().start(); 


						setTransparent(addPan, 100);

					}
					else if (apx+addPan.getWidth()/2 >= frameWidth || apy+addPan.getHeight()/2 >= frameHeight) {
						System.out.println("나갔다!!");
						addPan.setLocation(apdfx, apdfy);

						deleteSchedule(temp.getYear(), temp.getMonth(), temp.getDay());

						setTransparent(addPan, 255);
					}
					else {
						// 사이즈 커지는 애니메이션
						new Anim_Expand().start(); 

						// 투명도 설정 (불투명하게)
						setTransparent(addPan, 255);


					}
					addPan.updateUI();
					System.out.println("Released (" + apx + ", " + apy + ")");
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					int tem_x = e.getX()-(addPan.getWidth()/2); // 이벤트 발생 좌표값중 x값 추출
					int tem_y = e.getY()-(addPan.getHeight()/2); // 이벤트 발생 좌표값중 y값 추출..

					apx = apx + tem_x;
					apy = apy + tem_y;

					addPan.setBounds(apx, apy, addPan.getWidth(), addPan.getHeight());
					addPan.updateUI();
				}  
			});

		}
	}

	// 투명도 설정 -> 반투명하게 하면 가끔씩 글씨체에 문제가 생김
	public void setTransparent(JPanel pan, int percent) {
		pan.setBackground(new Color(
				pan.getBackground().getRed(), 
				pan.getBackground().getGreen(), 
				pan.getBackground().getBlue(),
				percent));
	}

	public void deleteSchedule(int y, int m, int d) {
		for(int i = 0 ; i < vSd.size() ; i++) {
			Schedule sc = vSd.elementAt(i);
			if(isSameDate(y, m, d, sc.getYear(), sc.getMonth(), sc.getDay())) {
				vSd.removeElementAt(i);
				return;
			}
		}
	}

	public boolean isSameDate(int y1, int m1, int d1, int y2, int m2, int d2) {
		if(y1 == y2 && m1 == m2 && d1 == d2)
			return true;
		return false;
	}

	private void addComponent(Container container,Component c,int x,int y,int width,int height)
	{
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	public static void main(String[] args) {
		new calendar();
	}
}
