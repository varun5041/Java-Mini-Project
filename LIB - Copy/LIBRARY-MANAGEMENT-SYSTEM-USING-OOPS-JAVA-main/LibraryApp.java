import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class LibraryApp extends JFrame {
    private final admin libraryAdmin;

    // Screens
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    // Login components
    private JTextField adminUserField;
    private JPasswordField adminPassField;
    private JTextField memberIdField;
    private JPasswordField memberPassField;

    // Admin dashboard components
    private JTable booksTable;
    private DefaultTableModel booksModel;
    private JTable membersTable;
    private DefaultTableModel membersModel;

    // Member dashboard state
    private member currentMember;
    private JTable memberBooksTable;
    private DefaultTableModel memberBooksModel;
    private JLabel memberWelcomeLabel;
    private DefaultTableModel requestsModel;

    public LibraryApp() {
        // Seed admin and some demo data
        this.libraryAdmin = new admin("Admin", 1);
        this.libraryAdmin.addbook(101, "Clean Code", "Robert C. Martin", 45.0);
        this.libraryAdmin.addbook(102, "Effective Java", "Joshua Bloch", 50.0);
        this.libraryAdmin.addbook(103, "Head First Design Patterns", "Eric Freeman", 40.0);
        this.libraryAdmin.AddMember(1001, "Alice");
        this.libraryAdmin.AddMember(1002, "Bob");

        setTitle("Library Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        root.add(buildLoginPanel(), "login");
        root.add(buildAdminPanel(), "admin");
        root.add(buildMemberPanel(), "member");
        setContentPane(root);

        showLogin();
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTabbedPane tabs = new JTabbedPane();

        // Admin login tab
        JPanel adminTab = new JPanel(new GridBagLayout());
        adminUserField = new JTextField("admin", 15);
        adminPassField = new JPasswordField("admin", 15);
        JButton adminLoginBtn = new JButton(new AbstractAction("Login as Admin") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = adminUserField.getText().trim();
                String pass = new String(adminPassField.getPassword());
                if ("admin".equalsIgnoreCase(user) && "admin".equals(pass)) {
                    currentMember = null;
                    refreshAdminTables();
                    cardLayout.show(root, "admin");
                } else {
                    JOptionPane.showMessageDialog(LibraryApp.this, "Invalid admin credentials");
                }
            }
        });
        gbc.gridx = 0; gbc.gridy = 0; adminTab.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; adminTab.add(adminUserField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; adminTab.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; adminTab.add(adminPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; adminTab.add(adminLoginBtn, gbc);

        // Member login tab
        JPanel memberTab = new JPanel(new GridBagLayout());
        memberIdField = new JTextField("1001", 15);
        memberPassField = new JPasswordField("member", 15);
        JButton memberLoginBtn = new JButton(new AbstractAction("Login as Member") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(memberIdField.getText().trim());
                    String pass = new String(memberPassField.getPassword());
                    member m = libraryAdmin.getMemberById(id);
                    if (m != null && "member".equals(pass)) {
                        currentMember = m;
                        if (memberWelcomeLabel != null) {
                            memberWelcomeLabel.setText("Welcome, " + m.Membername + " (ID " + m.MemberId + ")");
                        }
                        refreshMemberTables();
                        cardLayout.show(root, "member");
                    } else {
                        JOptionPane.showMessageDialog(LibraryApp.this, "Invalid member credentials");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryApp.this, "Enter a valid member ID");
                }
            }
        });
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 0; memberTab.add(new JLabel("Member ID"), gbc);
        gbc.gridx = 1; memberTab.add(memberIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; memberTab.add(new JLabel("Password"), gbc);
        gbc.gridx = 1; memberTab.add(memberPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; memberTab.add(memberLoginBtn, gbc);

        tabs.addTab("Admin", adminTab);
        tabs.addTab("Member", memberTab);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(tabs, gbc);
        return panel;
    }

    private JPanel buildAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddMember = new JButton("Add Member");
        JButton btnRemoveMember = new JButton("Remove Member");
        JButton btnViewMembers = new JButton("View Members");
        JButton btnShowAllBooks = new JButton("Show All Books");
        JButton btnAddBook = new JButton("Add Book");
        JButton btnRemoveBook = new JButton("Remove Book");
        JButton logout = new JButton("Logout");

        // Button actions
        btnAddMember.addActionListener(e -> onAddMember());
        btnRemoveMember.addActionListener(e -> onRemoveMember());
        btnViewMembers.addActionListener(e -> refreshAdminTables());
        btnShowAllBooks.addActionListener(e -> refreshAdminTables());
        btnAddBook.addActionListener(e -> onAddBook());
        btnRemoveBook.addActionListener(e -> onRemoveBook());
        logout.addActionListener(e -> showLogin());

        top.add(btnAddMember);
        top.add(btnRemoveMember);
        top.add(btnViewMembers);
        top.add(btnShowAllBooks);
        top.add(btnAddBook);
        top.add(btnRemoveBook);
        top.add(Box.createHorizontalStrut(16));
        top.add(logout);
        panel.add(top, BorderLayout.NORTH);

        // Center split: books and members
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        booksModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Price", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        booksTable = new JTable(booksModel);
        split.setTopComponent(new JScrollPane(booksTable));

        JPanel bottom = new JPanel(new BorderLayout());
        membersModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        membersTable = new JTable(membersModel);
        bottom.add(new JScrollPane(membersTable), BorderLayout.CENTER);

        // Requests panel for admin notifications
        requestsModel = new DefaultTableModel(new Object[]{"ReqID", "Member", "BookID", "Title", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable reqTable = new JTable(requestsModel);
        JPanel reqPanel = new JPanel(new BorderLayout());
        reqPanel.add(new JLabel("Pending Requests"), BorderLayout.NORTH);
        reqPanel.add(new JScrollPane(reqTable), BorderLayout.CENTER);
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");
        approve.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this, "Approve Request ID:");
            if (s == null) return;
            try {
                int rid = Integer.parseInt(s.trim());
                boolean ok = libraryAdmin.approveRequest(rid);
                if (!ok) JOptionPane.showMessageDialog(this, "Approve failed");
                refreshAdminTables();
                fillRequests(requestsModel);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric Request ID");
            }
        });
        reject.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this, "Reject Request ID:");
            if (s == null) return;
            try {
                int rid = Integer.parseInt(s.trim());
                boolean ok = libraryAdmin.rejectRequest(rid);
                if (!ok) JOptionPane.showMessageDialog(this, "Reject failed");
                fillRequests(requestsModel);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric Request ID");
            }
        });
        JPanel reqActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reqActions.add(approve); reqActions.add(reject);
        reqPanel.add(reqActions, BorderLayout.SOUTH);

        JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplit.setLeftComponent(new JScrollPane(membersTable));
        bottomSplit.setRightComponent(reqPanel);
        bottomSplit.setDividerLocation(300);
        bottom.add(bottomSplit, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField issueBookId = new JTextField(6);
        JTextField issueMemberId = new JTextField(6);
        JButton issueBtn = new JButton("Issue Book");
        issueBtn.addActionListener(e -> {
            try {
                int bid = Integer.parseInt(issueBookId.getText().trim());
                int mid = Integer.parseInt(issueMemberId.getText().trim());
                boolean ok = libraryAdmin.issueBookToMember(bid, mid);
                if (!ok) JOptionPane.showMessageDialog(this, "Issue failed. Check availability and IDs.");
                refreshAdminTables();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric IDs");
            }
        });

        JTextField returnBookId = new JTextField(6);
        JTextField returnMemberId = new JTextField(6);
        JButton returnBtn = new JButton("Return Book");
        returnBtn.addActionListener(e -> {
            try {
                int bid = Integer.parseInt(returnBookId.getText().trim());
                int mid = Integer.parseInt(returnMemberId.getText().trim());
                boolean ok = libraryAdmin.returnBookFromMember(bid, mid);
                if (!ok) JOptionPane.showMessageDialog(this, "Return failed. Check IDs.");
                refreshAdminTables();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric IDs");
            }
        });

        actions.add(new JLabel("Issue BookID:")); actions.add(issueBookId);
        actions.add(new JLabel("to MemberID:")); actions.add(issueMemberId);
        actions.add(issueBtn);
        actions.add(Box.createHorizontalStrut(12));
        actions.add(new JLabel("Return BookID:")); actions.add(returnBookId);
        actions.add(new JLabel("from MemberID:")); actions.add(returnMemberId);
        actions.add(returnBtn);
        bottom.add(actions, BorderLayout.SOUTH);

        split.setBottomComponent(bottom);
        split.setDividerLocation(300);
        panel.add(split, BorderLayout.CENTER);

        // initial fill
        fillRequests(requestsModel);
        return panel;
    }

    private void onAddMember() {
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Member ID:", id, "Name:", name}, "Add Member", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int mid = Integer.parseInt(id.getText().trim());
                String n = name.getText().trim();
                if (n.isEmpty()) { JOptionPane.showMessageDialog(this, "Name required"); return; }
                libraryAdmin.AddMember(mid, n);
                refreshAdminTables();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric ID");
            }
        }
    }

    private void onRemoveMember() {
        String s = JOptionPane.showInputDialog(this, "Member ID to remove:");
        if (s == null) return;
        try {
            int mid = Integer.parseInt(s.trim());
            libraryAdmin.removemember(mid);
            refreshAdminTables();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric ID");
        }
    }

    private void onAddBook() {
        JTextField id = new JTextField();
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField price = new JTextField();
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Book ID:", id, "Title:", title, "Author:", author, "Price:", price}, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int bid = Integer.parseInt(id.getText().trim());
                String t = title.getText().trim();
                String a = author.getText().trim();
                double p = Double.parseDouble(price.getText().trim());
                if (t.isEmpty() || a.isEmpty()) { JOptionPane.showMessageDialog(this, "Title and Author required"); return; }
                libraryAdmin.addbook(bid, t, a, p);
                refreshAdminTables();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric values");
            }
        }
    }

    private void onRemoveBook() {
        String s = JOptionPane.showInputDialog(this, "Book ID to remove:");
        if (s == null) return;
        try {
            int bid = Integer.parseInt(s.trim());
            // quick remove using existing console path: simulate input would be messy; do manual remove
            java.util.Iterator<Book> it = libraryAdmin.getBooks().iterator();
            boolean removed = false;
            while (it.hasNext()) {
                Book b = it.next();
                if (b.BookId == bid) { it.remove(); removed = true; break; }
            }
            if (!removed) JOptionPane.showMessageDialog(this, "Book not found");
            refreshAdminTables();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric ID");
        }
    }

    private JPanel buildMemberPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        memberWelcomeLabel = new JLabel("Welcome");
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> showLogin());
        top.add(memberWelcomeLabel); top.add(Box.createHorizontalStrut(16)); top.add(logout);
        panel.add(top, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // All books (read-only view with search)
        JPanel allBooksPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Title", "Author"});
        JButton showAllBtn = new JButton("Show All Books");
        JButton searchBtn = new JButton("Search");
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.add(showAllBtn);
        searchBar.add(Box.createHorizontalStrut(8));
        searchBar.add(searchType); searchBar.add(searchField); searchBar.add(searchBtn);
        allBooksPanel.add(searchBar, BorderLayout.NORTH);
        DefaultTableModel allBooksModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Price", "Availability"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable allBooksTable = new JTable(allBooksModel);
        allBooksPanel.add(new JScrollPane(allBooksTable), BorderLayout.CENTER);
        searchBtn.addActionListener(e -> doMemberSearch(searchType, searchField, allBooksModel));
        showAllBtn.addActionListener(e -> fillAllBooksForMember(allBooksModel));

        // Borrowed books of current member
        JPanel borrowedPanel = new JPanel(new BorderLayout());
        memberBooksModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Price"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        memberBooksTable = new JTable(memberBooksModel);
        borrowedPanel.add(new JScrollPane(memberBooksTable), BorderLayout.CENTER);

        JPanel memberActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton requestBtn = new JButton("Request Book");
        requestBtn.addActionListener(e -> onRequestBook());
        memberActions.add(requestBtn);
        borrowedPanel.add(memberActions, BorderLayout.SOUTH);

        split.setTopComponent(allBooksPanel);
        split.setBottomComponent(borrowedPanel);
        split.setDividerLocation(300);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void doAdminSearch(JComboBox<String> searchType, JTextField searchField) {
        String mode = (String) searchType.getSelectedItem();
        String q = searchField.getText().trim();
        ArrayList<Book> result = "Author".equals(mode)
                ? libraryAdmin.searchBooksByAuthor(q)
                : libraryAdmin.searchBooksByTitle(q);
        fillBooksTable(result);
    }

    private void doMemberSearch(JComboBox<String> searchType, JTextField searchField, DefaultTableModel model) {
        String mode = (String) searchType.getSelectedItem();
        String q = searchField.getText().trim();
        ArrayList<Book> result = "Author".equals(mode)
                ? libraryAdmin.searchBooksByAuthor(q)
                : libraryAdmin.searchBooksByTitle(q);
        model.setRowCount(0);
        for (Book b : result) {
            String avail = availabilityString(b);
            model.addRow(new Object[]{b.BookId, b.Title, b.Author, b.Price, avail});
        }
        // Also inform admin view about any changes in data (no-op for search, but keeps UI coherent if we later track member-side actions)
        if (requestsModel != null) fillRequests(requestsModel);
    }

    private void refreshAdminTables() {
        fillBooksTable(libraryAdmin.getBooks());
        fillMembersTable(libraryAdmin.getMembers());
    }

    private void fillBooksTable(java.util.List<Book> list) {
        if (booksModel == null) return;
        booksModel.setRowCount(0);
        for (Book b : list) {
            String status = issuedStatus(b);
            booksModel.addRow(new Object[]{b.BookId, b.Title, b.Author, b.Price, status});
        }
    }

    private void fillMembersTable(java.util.List<member> list) {
        if (membersModel == null) return;
        membersModel.setRowCount(0);
        for (member m : list) {
            membersModel.addRow(new Object[]{m.MemberId, m.Membername});
        }
    }

    private void fillRequests(DefaultTableModel reqModel) {
        reqModel.setRowCount(0);
        for (BookRequest r : libraryAdmin.getRequests()) {
            reqModel.addRow(new Object[]{r.requestId, r.memberName + " (" + r.memberId + ")", r.bookId, r.bookTitle, r.status});
        }
    }

    private void refreshMemberTables() {
        if (currentMember == null) return;
        memberBooksModel.setRowCount(0);
        for (Book b : currentMember.borrowedbooks) {
            memberBooksModel.addRow(new Object[]{b.BookId, b.Title, b.Author, b.Price});
        }
    }

    private void showLogin() {
        cardLayout.show(root, "login");
    }

    private String issuedStatus(Book b) {
        if (!b.Issued) return "Available";
        String suffix = "";
        if (b.IssuedToMemberId != null) {
            member m = libraryAdmin.getMemberById(b.IssuedToMemberId);
            if (m != null) {
                suffix = " (" + m.Membername + ")";
            }
        }
        return "Issued to " + b.IssuedToMemberId + suffix;
    }

    private String availabilityString(Book b) {
        return b.Issued ? "Not available" : "Available";
    }

    private void fillAllBooksForMember(DefaultTableModel model) {
        model.setRowCount(0);
        for (Book b : libraryAdmin.getBooks()) {
            model.addRow(new Object[]{b.BookId, b.Title, b.Author, b.Price, availabilityString(b)});
        }
    }

    private void onRequestBook() {
        if (currentMember == null) { JOptionPane.showMessageDialog(this, "Please login as member first"); return; }
        JTextField bookId = new JTextField();
        JTextField title = new JTextField();
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Book ID:", bookId, "Title:", title}, "Request Book", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int bid = Integer.parseInt(bookId.getText().trim());
                String t = title.getText().trim();
                BookRequest r = libraryAdmin.submitRequest(currentMember.MemberId, bid, t);
                if (r == null) {
                    JOptionPane.showMessageDialog(this, "Failed to submit request");
                } else {
                    JOptionPane.showMessageDialog(this, "Request submitted: #" + r.requestId);
                    if (requestsModel != null) fillRequests(requestsModel);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric Book ID");
            }
        }
    }
}


