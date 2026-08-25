# Hotel Management System — Setup & Run Guide

Everything is in this one folder, ready to compile and run.
Follow these steps **in order**. Don't skip ahead — each step depends
on the one before it.

```
HotelManagementSystem/
├── database/
│   └── create_database.sql   ← run this in MySQL first
├── lib/
│   └── (put the MySQL driver .jar here — Step 2)
├── src/
│   └── (23 .java files — the whole app)
└── README.md                 ← you are here
```

---

## STEP 1 — Set up the database

1. Install **MySQL Community Server** if you don't have it
   (mysql.com → Downloads). **MySQL Workbench** (the visual tool) is
   recommended too.
2. Open **MySQL Workbench**, connect to your local server.
3. File → Open SQL Script → choose `database/create_database.sql`.
4. Click the ⚡ **Execute** button to run the whole file.
5. You should see it create the database, 5 tables, and insert sample
   rooms, guests, and two login accounts with no errors.

✅ Database is ready.

---

## STEP 2 — Download the MySQL JDBC driver

This is the missing piece that lets Java actually talk to MySQL.

1. Go to: https://dev.mysql.com/downloads/connector/j/
2. Under "Select Operating System," choose **"Platform Independent."**
3. Download the **.zip** (or .tar.gz).
4. Unzip it. Find the file that looks like:
   `mysql-connector-j-9.x.x.jar`
5. Copy that single `.jar` file into this project's `lib/` folder.

You should now have something like:
`HotelManagementSystem/lib/mysql-connector-j-9.1.0.jar`

---

## STEP 3 — Set your MySQL password in the code

1. Open `src/DatabaseConnection.java` in any text editor.
2. Find this line:
   ```java
   private static final String PASSWORD = "your_password_here";
   ```
3. Replace `your_password_here` with your actual MySQL root password.
4. Save the file.

(If your MySQL username isn't `root`, change the `USERNAME` line too.)

---

## STEP 4 — Compile the whole project

Open a terminal / command prompt **inside the `HotelManagementSystem`
folder** and run:

**Windows:**
```
javac -cp "lib\mysql-connector-j-9.x.x.jar" -d out src\*.java
```

**Mac/Linux:**
```
javac -cp "lib/mysql-connector-j-9.x.x.jar" -d out src/*.java
```

(Replace `9.x.x` with your driver's real version number — check the
exact filename inside your `lib` folder.)

This creates an `out/` folder full of compiled `.class` files.
If you see NO red error text, it worked.

---

## STEP 5 — Run the application

**Windows:**
```
java -cp "out;lib\mysql-connector-j-9.x.x.jar" Main
```

**Mac/Linux:**
```
java -cp "out:lib/mysql-connector-j-9.x.x.jar" Main
```

The **Login window** should appear.

### Sample logins (already in the database):
| Username | Password | Role  |
|----------|----------|-------|
| admin    | admin123 | Owner (sees all tabs, including Reports) |
| staff    | staff123 | Staff (sees all tabs except Reports)     |

---

## 💡 Easier option: use an IDE instead of the terminal

If typing commands feels fiddly, use **IntelliJ IDEA Community**
(free) or **Eclipse**:

1. Create a new Java project, and copy all files from `src/` into
   its `src` folder.
2. Right-click the project → **Open Module Settings** (IntelliJ) or
   **Build Path** (Eclipse) → **Add External JAR** → select your
   `mysql-connector-j-9.x.x.jar`.
3. Right-click `Main.java` → **Run**.

---

## What each part of the app does

| Tab | What it covers |
|---|---|
| **Rooms** | Add, view, search, edit, and deactivate rooms |
| **Guests** | Add guests, view/search the guest list, view a guest's stay history |
| **Reservations** | Book a room for a guest, checking availability first |
| **Check-In / Check-Out** | Move a booking through its stay, updates room status automatically |
| **Payments** | Record a payment, see the running balance and payment history |
| **Reports** *(Owner login only)* | Daily revenue, room occupancy %, guest history, reservation counts |

---

## Troubleshooting

**"FAILED to connect to the database" / login screen shows a
Database Error:**
- Is MySQL actually running? (Check MySQL Workbench can connect.)
- Is the password in `DatabaseConnection.java` correct?
- Did Step 1's SQL script finish with no errors?

**`javac` or `java` says "not recognized" / "command not found":**
- You need the **JDK** installed (not just the JRE), and it needs to
  be on your system PATH. Download from adoptium.net if unsure.

**Compile errors mentioning `com.mysql.cj...`:**
- Double check the `-cp` path in Step 4 points to the exact `.jar`
  filename inside your `lib` folder (version numbers must match).

---

## Two honest notes for later

1. **Passwords are stored as plain text** in the `users` table right
   now — that's fine while you're learning, but a real system should
   hash passwords (look up "BCrypt Java" when you're ready for that).
2. **This code was written carefully but not compiled by me** — the
   sandbox I built it in only has a Java *runtime*, not a compiler,
   and no internet to install one. I checked every file by hand
   (matching braces/parentheses, matching imports, matching method
   names between files), but you are the first to actually compile
   it. If Step 4 throws an error, paste the exact error message back
   to me and I'll fix it immediately — that's a completely normal
   part of building software, not a sign anything is broken.

---

## What's next (Phase 9 remaining items)

The app already has: login, owner/staff permissions, basic
validation, and error-handling message boxes. Two Phase-9 items are
still genuinely up to you and your own machine, since they're not
code:
- **Backup** — in MySQL Workbench: Server → Data Export → pick
  `hotel_management` → export to a `.sql` file regularly.
- **Desktop shortcut** — create a `.bat` (Windows) or `.sh` (Mac/Linux)
  file that runs the Step 5 command, then make a shortcut to it.

Want me to write that backup script or launcher shortcut for you next?
