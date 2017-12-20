package tazo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;;

public class Application {

	static final String SERVER_NAME = "147.46.15.147";
	static final String DB_NAME = "db2013-11384";
	static final String USER_NAME = "u2013-11384";
	static final String PASSWORD = "317f633e2c7d";
	
	static final String INTEGER_CONVERT_ERROR = "Input is not Integer";
	static final String INVALID_ACTION = "Invalid action";
	
	static Connection conn = null;
	
	/* scheme */
	// stage : id(int), name(char 200), location(char 200), capacity(int >= 1)
	// concert : id(int), name(char 200), type(char 200), price(int >=0), booked
	// audience : id(int), name(char 200), sex(char 'M' or 'F'), age(int >= 1), cid
	// assign : s.id(int), c.id(int)
	// book : c.id(int), a.id(int), seat(int)
	
	public static void main(String[] args) {
		BufferedReader br = null;

		openDatabase();
		if(conn == null) {
			System.out.println("Fail to conncect database");
			return;
		}
		
		// dropTables();
		createTables();
		
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			
			showQueryList();
			
			while (true) {
				System.out.print("Select your action: ");
				String input = br.readLine();
				int actionid = -1;
				
				try {
					actionid = Integer.valueOf(input);
				} catch (NumberFormatException e) {
					System.out.println(INVALID_ACTION);
					continue;
				}
				
				if(actionid < 1 || actionid > 15) {
					System.out.println(INVALID_ACTION);
					continue;
				}
				
				switch(actionid) {
				// 1. print all buildings
				case 1:
					selectAllFromStage();
					break;
					
				// 2. print all performances
				case 2:
					selectAllFromConcert();
					break;
					
				// 3. print all audiences
				case 3:
					selectAllFromAudience();
					break;
					
				// 4. insert a new building
				case 4:
				{
					System.out.print("Building name: " );
					String name = br.readLine();
					System.out.print("Building location: " );
					String location = br.readLine();
					System.out.print("Building capacity: " );
					String capstr = br.readLine();
					
					try {
						int capacity = Integer.valueOf(capstr);
						insertIntoStage(name, location, capacity);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 5. remove a building
				case 5:
				{
					System.out.print("Building ID: " );
					String idstr = br.readLine();
					try {
						int id = Integer.valueOf(idstr);
						deleteFromStage(id);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 6. insert a new performance
				case 6:
				{
					System.out.print("Performance name: " );
					String name = br.readLine();
					System.out.print("Performance type: " );
					String type = br.readLine();
					System.out.print("Performance price: " );
					String pricestr = br.readLine();
					
					try {
						int price = Integer.valueOf(pricestr);
						insertIntoConcert(name, type, price);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 7. remove a performance
				case 7:
				{
					System.out.print("Performance ID: " );
					String idstr = br.readLine();
					try {
						int id = Integer.valueOf(idstr);
						deleteFromConcert(id);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 8. insert a new audience
				case 8:
				{
					System.out.print("Audience name: " );
					String name = br.readLine();
					System.out.print("Audience gender: " );
					String gender = br.readLine();
					System.out.print("Audience age: " );
					String agestr = br.readLine();
					
					try {
						int age = Integer.valueOf(agestr);
						insertIntoAudience(name, gender, age);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 9. remove an audience
				case 9:
				{
					System.out.print("Audience ID: " );
					String idstr = br.readLine();
					try {
						int id = Integer.valueOf(idstr);
						deleteFromAudience(id);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 10. assign a performance to a building
				case 10:
				{
					System.out.print("Building ID: " );
					String sidstr = br.readLine();
					System.out.print("Performance ID: " );
					String cidstr = br.readLine();
					try {
						int sid = Integer.valueOf(sidstr);
						int cid = Integer.valueOf(cidstr);
						insertIntoAssign(sid, cid);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 11. book a performance
				case 11:
				{
					System.out.print("Performance ID: " );
					String cidstr = br.readLine();
					System.out.print("Audience ID: " );
					String aidstr = br.readLine();
					System.out.print("Seat number: " );
					String seatstr = br.readLine();
					try {
						int cid = Integer.valueOf(cidstr);
						int aid = Integer.valueOf(aidstr);
						ArrayList<Integer> seatList = new ArrayList<>();
						String[] list = seatstr.split(",");
						for(String s : list) {
							int k = Integer.valueOf(s.trim());
							seatList.add(k);
						}
						insertIntoBook(cid, aid, seatList);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 12. print all performances which assigned at a building
				case 12:
				{
					System.out.print("Building ID: " );
					String sidstr = br.readLine();
					try {
						int sid = Integer.valueOf(sidstr);
						selectAllAssignedStage(sid);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 13. print all audiences who booked for a performance
				case 13:
				{
					System.out.print("Performance ID: " );
					String cidstr = br.readLine();
					try {
						int cid = Integer.valueOf(cidstr);
						selectAllBookedAudience(cid);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				// 14. print ticket booking status of a performance
				case 14:
				{
					System.out.print("Performance ID: " );
					String cidstr = br.readLine();
					try {
						int cid = Integer.valueOf(cidstr);
						selectSeatInformation(cid);
					} catch (NumberFormatException e) {
						System.out.println(INTEGER_CONVERT_ERROR);
					}
				}
					break;
					
				default:
					break;
				}
				
				if(actionid == 15)
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		System.out.println("Bye!");
	}
	
	public static void showQueryList() {
		System.out.println("============================================================");
		System.out.println("1. print all buildings");
		System.out.println("2. print all performances");
		System.out.println("3. print all audiences");
		System.out.println("4. insert a new building");
		System.out.println("5. remove a building");
		System.out.println("6. insert a new performance");
		System.out.println("7. remove a performance");
		System.out.println("8. insert a new audience");
		System.out.println("9. remove an audience");
		System.out.println("10. assign a performance to a building");
		System.out.println("11. book a performance");
		System.out.println("12. print all performances which assigned at a building");
		System.out.println("13. print all audiences who booked for a performance");
		System.out.println("14. print ticket booking status of a performance");
		System.out.println("15. exit");
		System.out.println("============================================================");
	}

	public static void openDatabase(){
		String url = "jdbc:mariadb://" + SERVER_NAME + "/" + DB_NAME;
		try {
			conn = DriverManager.getConnection(url, USER_NAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void dropTables(){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "DROP TABLE IF EXISTS book, assign, audience, concert, stage";
		
		try {
			stmt = conn.prepareStatement(sql);
			int count = stmt.executeUpdate();
			System.out.println("drop table: " + count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public static void createTables(){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String sqls[] = {
				"CREATE TABLE IF NOT EXISTS stage(id int NOT NULL AUTO_INCREMENT, name varchar(200), location varchar(200), capacity int, PRIMARY KEY (id))",
				"CREATE TABLE IF NOT EXISTS concert(id int NOT NULL AUTO_INCREMENT, name varchar(200), type varchar(200), price int, PRIMARY KEY (id))",
				"CREATE TABLE IF NOT EXISTS audience(id int NOT NULL AUTO_INCREMENT, name varchar(200), sex varchar(1), age int, PRIMARY KEY (id))",
				"CREATE TABLE IF NOT EXISTS assign(sid int, cid int, FOREIGN KEY(sid) REFERENCES stage(id) ON DELETE CASCADE, FOREIGN KEY (cid) REFERENCES concert(id) ON DELETE CASCADE, PRIMARY KEY (cid))",
				"CREATE TABLE IF NOT EXISTS book(cid int, aid int, seat int, FOREIGN KEY(cid) REFERENCES concert(id) ON DELETE CASCADE, FOREIGN KEY (aid) REFERENCES audience(id) ON DELETE CASCADE, PRIMARY KEY (cid, seat))",
		};
		
		for(int i=0;i<5;i++) {
			try {
				stmt = conn.prepareStatement(sqls[i]);
				rs = stmt.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if(rs != null) {
					try {
						rs.close();
					} catch (SQLException ee) {
						ee.printStackTrace();
					}
				}
				if(stmt != null) {
					try {
						stmt.close();
					} catch (SQLException ee) {
						ee.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void selectAllFromStage()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT stage.*, count(cid) AS assigned FROM stage LEFT JOIN assign ON id=sid GROUP BY id";
		System.out.println("---------------------------------------------------");
		System.out.println("id	name				location		capacity		assigned");
		System.out.println("---------------------------------------------------");
		
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String location = rs.getString("location");
				int capacity = rs.getInt("capacity");
				int assigned = rs.getInt("assigned");
				
				System.out.printf("%-6d%-20s%-20s%-6d%-6d\n", id, name, location, capacity, assigned);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
			
		System.out.println("---------------------------------------------------");
	}

	public static void selectAllFromConcert()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT concert.*, count(aid) as booked FROM concert LEFT JOIN book ON id=cid GROUP BY id";
		System.out.println("------------------------------------------------------");
		System.out.println("id	name				type		price		booked");
		System.out.println("------------------------------------------------------");
		
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String type = rs.getString("type");
				int price = rs.getInt("price");
				int booked = rs.getInt("booked");
				
				System.out.printf("%-6d%-20s%-20s%-15d%-6d\n", id, name, type, price, booked);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
			
		System.out.println("------------------------------------------------------");
	}

	public static void selectAllFromAudience()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM audience";
		System.out.println("----------------------------------------------");
		System.out.println("id	name				gender		age");
		System.out.println("----------------------------------------------");
		
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String sex = rs.getString("sex");
				int age = rs.getInt("age");
				
				System.out.printf("%-6d%-20s%-10s%-6d\n", id, name, sex, age);
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
		System.out.println("----------------------------------------------");
	}
	
	public static void insertIntoStage(String name, String location, int capacity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO stage(name, location, capacity) VALUES(?, ?, ?)";
		int count = 0;
		
		if(capacity <= 0)
		{
			System.out.println("Capacity should be larger than 0");
			return;
		}
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, location);
			stmt.setInt(3, capacity);
			count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("A building is successfully inserted");
			else
				System.out.println("Fail to insert a building: " + count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public static void insertIntoConcert(String name, String type, int price) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO concert(name, type, price) VALUES(?, ?, ?)";
		int count = 0;
		
		if(price < 0)
		{
			System.out.println("Price should be 0 or more");
			return;
		}
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, type);
			stmt.setInt(3, price);
			count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("A performance is successfully inserted");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public static void insertIntoAudience(String name, String sex, int age) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO audience(name, sex, age) VALUES(?, ?, ?)";
		
		if(!sex.equals("M") && !sex.equals("F")) {
			System.out.println("Gender should be 'M' or 'F'");
			return;
		}
		
		if(age <= 0) {
			System.out.println("Age should be more than 0");
			return;
		}
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, sex);
			stmt.setInt(3, age);
			int count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("An audience is successfully inserted");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteFromStage(int id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM stage WHERE id=?";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("A building is successfully removed");
			else
				System.out.println("Building " + id + " doesn¡¯t exist");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public static void deleteFromConcert(int id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM concert WHERE id=?";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("A performance is successfully removed");
			else
				System.out.println("Performance " + id + " doesn¡¯t exist");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	public static void deleteFromAudience(int id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM audience WHERE id=?";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("An audience is successfully removed");
			else
				System.out.println("Audience " + id + " doesn¡¯t exist");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	public static void insertIntoAssign(int sid, int cid) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO assign VALUES(?, ?)";
		int count = 0;
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, sid);
			stmt.setInt(2, cid);
			count = stmt.executeUpdate();
			if(count > 0)
				System.out.println("Successfully assign a performance");
		} catch (SQLException e) {
			// already assigned
			if(e.getMessage().contains("Duplicate")) {
				System.out.println("Performance " + cid + " is already assigned");
			} else {
				System.out.println("Performance " + cid + " isn't assigned");
			}
			// e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
	}
	
	public static void insertIntoBook(int cid, int aid, ArrayList<Integer> seatList) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql;
		StringBuilder sb = new StringBuilder("INSERT INTO book VALUES");
		int price = 0;
		int age = 0;
		int capacity = 0;
		int count = 0;	
		
		sql = "SELECT price, age, capacity FROM stage, concert, audience, assign WHERE concert.id=? and audience.id=? and assign.cid=concert.id and stage.id=assign.sid";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, cid);
			stmt.setInt(2, aid);
			rs = stmt.executeQuery();
			
			// assert, #result is 0 or 1
			while(rs.next()) {
				price = rs.getInt("price");
				age = rs.getInt("age");
				capacity = rs.getInt("capacity");
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
		
		if(age == 0 || capacity == 0) {
			System.out.println("Performance " + cid + " or Audience " + aid + " don't exist");
			return;
		}

		// repeatedly append (cid, aid, seat),  to INSERT at once
		for(int seat : seatList) {
			if(seat < 1 || seat > capacity) {
				System.out.println("Seat number out of range");
				return;
			}
			String str = String.format("(%d, %d, %d),", cid, aid, seat);
			sb.append(str);
		}
		// remove last comma(,)
		sb.setLength(sb.length() - 1);
		
		sql = sb.toString();
		try {
			stmt = conn.prepareStatement(sql);
			count = stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("The seat is already taken");
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
		
		if(count > 0) {
			double rate = 0;
			if(age >= 1 && age <= 7)
				rate = 0;
			else if(age >=8 && age <= 12)
				rate = 0.5;
			else if(age >=13 && age <= 18)
				rate = 0.8;
			else
				rate = 1;
			double totalprice = seatList.size() * price * rate;	
			
			System.out.println("Successfully book a performance");
			System.out.printf("Total ticket price is %d\n", Math.round(totalprice));
		}
	}

	public static void selectAllAssignedStage(int sid) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT concert.*, count(aid) as booked FROM concert LEFT JOIN book ON id=cid, assign WHERE assign.sid=? and concert.id=assign.cid GROUP BY id";
		System.out.println("---------------------------------------------------");
		System.out.println("id	name				type		price		booked");
		System.out.println("---------------------------------------------------");
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, sid);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String type = rs.getString("type");
				int price = rs.getInt("price");
				int booked = rs.getInt("booked");
				
				System.out.printf("%-6d%-20s%-20s%-15d%-6d\n", id, name, type, price, booked);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
			
		System.out.println("---------------------------------------------------");
	}
	
	public static void selectAllBookedAudience(int cid) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM audience, book WHERE book.cid=? and audience.id=book.aid GROUP BY audience.id";
		System.out.println("----------------------------------------------");
		System.out.println("id	name				gender		age");
		System.out.println("----------------------------------------------");
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, cid);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String sex = rs.getString("sex");
				int age = rs.getInt("age");
				
				System.out.printf("%-6d%-20s%-10s%-6d\n", id, name, sex, age);
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
		System.out.println("----------------------------------------------");
	}
	
	public static void selectSeatInformation(int cid) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int capacity = 0;
		
		String sql = "SELECT capacity FROM stage, assign WHERE sid=id and cid=?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, cid);
			rs = stmt.executeQuery();
			while(rs.next()) {
				capacity = rs.getInt("capacity");
				break;
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
		
		if(capacity == 0) {
			System.out.println("Performance " + cid + " isn't assigned");
			return;
		}
		
		sql = "SELECT * FROM book WHERE book.cid=?";
		System.out.println("----------------------------------------------");
		System.out.println("seat_number			audience_id");
		System.out.println("----------------------------------------------");
		
		int idx = 1;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, cid);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int seat = rs.getInt("seat");
				int aid = rs.getInt("aid");
				
				while(idx < seat) {
					System.out.printf("%-6d\n", idx++);
				}
				System.out.printf("%-6d%-6d\n", seat, aid);
				idx = seat + 1;
			}
			while(idx <= capacity) {
				System.out.printf("%-6d\n", idx++);
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
			
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ee) {
					ee.printStackTrace();
				}
			}
		}
		System.out.println("----------------------------------------------");
	}

}