CREATE TABLE Users (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE UserSessions (
    session_id VARCHAR(100) PRIMARY KEY,
    user_id INT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    login_time TIMESTAMP NOT NULL,
    last_activity TIMESTAMP NOT NULL,
    expired BOOLEAN DEFAULT false,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

ALTER TABLE UserSessions ADD COLUMN logout_time TIMESTAMP;

-- Create UserActivities table
CREATE TABLE UserActivities (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    user_id INT,
    username VARCHAR(50) NOT NULL,
    session_id VARCHAR(100),
    action VARCHAR(100) NOT NULL,
    action_details VARCHAR(500),
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILURE, WARNING
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_activity_session FOREIGN KEY (session_id) REFERENCES UserSessions(session_id)
);

-- Create SuspiciousActivities table
CREATE TABLE SuspiciousActivities (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    activity_id INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    reviewed BOOLEAN DEFAULT false,
    reviewed_by INT,
    review_timestamp TIMESTAMP,
    CONSTRAINT fk_suspicious_activity FOREIGN KEY (activity_id) REFERENCES UserActivities(id),
    CONSTRAINT fk_reviewer FOREIGN KEY (reviewed_by) REFERENCES Users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_activities_user ON UserActivities(user_id);
CREATE INDEX idx_activities_session ON UserActivities(session_id);
CREATE INDEX idx_activities_timestamp ON UserActivities(timestamp);
CREATE INDEX idx_sessions_user ON UserSessions(user_id);
CREATE INDEX idx_sessions_expired ON UserSessions(expired);

CREATE TABLE UserSessions (
    session_id VARCHAR(100) PRIMARY KEY,
    user_id INT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    login_time TIMESTAMP NOT NULL,
    last_activity TIMESTAMP NOT NULL,
    expired BOOLEAN DEFAULT false,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Create UserActivities table
CREATE TABLE UserActivities (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    user_id INT,
    username VARCHAR(50) NOT NULL,
    session_id VARCHAR(100),
    action VARCHAR(100) NOT NULL,
    action_details VARCHAR(500),
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILURE, WARNING
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT fk_activity_session FOREIGN KEY (session_id) REFERENCES UserSessions(session_id)
);

-- Create SuspiciousActivities table
CREATE TABLE SuspiciousActivities (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    activity_id INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    reviewed BOOLEAN DEFAULT false,
    reviewed_by INT,
    review_timestamp TIMESTAMP,
    CONSTRAINT fk_suspicious_activity FOREIGN KEY (activity_id) REFERENCES UserActivities(id),
    CONSTRAINT fk_reviewer FOREIGN KEY (reviewed_by) REFERENCES Users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_activities_user ON UserActivities(user_id);
CREATE INDEX idx_activities_session ON UserActivities(session_id);
CREATE INDEX idx_activities_timestamp ON UserActivities(timestamp);
CREATE INDEX idx_sessions_user ON UserSessions(user_id);
CREATE INDEX idx_sessions_expired ON UserSessions(expired);

CREATE TABLE Roles (
    role_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE AuditLog (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    action VARCHAR(100),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE PasswordResetTokens (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    token VARCHAR(255),
    expiry TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE Category (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Author (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(200) NOT NULL
);

CREATE TABLE Publisher (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(200) NOT NULL
);

CREATE TABLE Book (
    isbn VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    publisher_id INT,
    publication_year INT,
    edition VARCHAR(50),
    language VARCHAR(50),
    call_number VARCHAR(100),
    number_of_pages INT,
    summary CLOB,
    format VARCHAR(20),
    FOREIGN KEY (publisher_id) REFERENCES Publisher(id)
);

CREATE TABLE BookAuthor (
    book_isbn VARCHAR(20),
    author_id INT,
    PRIMARY KEY (book_isbn, author_id),
    FOREIGN KEY (book_isbn) REFERENCES Book(isbn),
    FOREIGN KEY (author_id) REFERENCES Author(id)
);

CREATE TABLE BookCategory (
    book_isbn VARCHAR(20),
    category_id INT,
    PRIMARY KEY (book_isbn, category_id),
    FOREIGN KEY (book_isbn) REFERENCES Book(isbn),
    FOREIGN KEY (category_id) REFERENCES Category(id)
);

CREATE TABLE Copies (
    copy_id VARCHAR(50) PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    acquisition_date DATE NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    condition VARCHAR(20) NOT NULL CHECK (condition IN ('NEW', 'GOOD', 'FAIR', 'DAMAGED')),
    location VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'CHECKED_OUT', 'ON_HOLD', 'LOST', 'UNDER_REPAIR')),
    FOREIGN KEY (isbn) REFERENCES Book(isbn) ON DELETE CASCADE
);


CREATE TABLE Supplier (
    supplier_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(255),
    payment_terms VARCHAR(100)
);


CREATE TABLE PurchaseRequest (
    request_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    title VARCHAR(255) NOT NULL,
    requested_by VARCHAR(100),
    request_date DATE,
    status VARCHAR(50) DEFAULT 'Pending'
);

CREATE TABLE PurchaseOrder (
    po_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    po_number VARCHAR(50) UNIQUE NOT NULL,
    supplier_id INT,
    created_date DATE,
    expected_delivery_date DATE,
    status VARCHAR(50) DEFAULT 'Created',
    FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id)
);

CREATE TABLE POItems (
    item_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    po_id INT,
    title VARCHAR(100),
    quantity INT,
    unit_cost DECIMAL(10,2),
    FOREIGN KEY (po_id) REFERENCES PurchaseOrder(po_id)
);

CREATE TABLE PurchaseOrderLine (
    po_line_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    po_id INT,
    title VARCHAR(255) NOT NULL,
    quantity INT,
    unit_cost DECIMAL(10,2),
    FOREIGN KEY (po_id) REFERENCES PurchaseOrder(po_id)
);

CREATE TABLE GoodsReceiptNote (
    grn_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    po_id INT,
    receipt_date DATE,
    invoice_number VARCHAR(100),
    invoice_date DATE,
    notes CLOB,
    FOREIGN KEY (po_id) REFERENCES PurchaseOrder(po_id)
);

CREATE TABLE Copy (
    copy_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    title VARCHAR(255) NOT NULL,
    po_id INT,
    status VARCHAR(50) DEFAULT 'Available',
    location VARCHAR(100),
    FOREIGN KEY (po_id) REFERENCES PurchaseOrder(po_id)
);

CREATE TABLE InventoryAdjustment (
    adjustment_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    copy_id INT,
    adjustment_type VARCHAR(50),
    reason VARCHAR(255),
    adjustment_date DATE,
    users VARCHAR(100),
    FOREIGN KEY (copy_id) REFERENCES Copy(copy_id)
);

CREATE TABLE Loan (
    loan_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    member_id INT NOT NULL,
    copy_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    staff_id INT,
    status VARCHAR(50) DEFAULT 'Checked Out'
);

CREATE TABLE Fine (
    fine_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    member_id INT NOT NULL,
    loan_id INT,
    amount DECIMAL(10,2) NOT NULL,
    date_levied DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Unpaid'
);

CREATE TABLE HoldReservation (
    hold_id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    member_id INT NOT NULL,
    copy_id INT NOT NULL,
    request_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending',
    pickup_expiry_date DATE
);

ALTER TABLE Book
ADD call_number VARCHAR(100);

ALTER TABLE Book
ADD number_of_pages INT;


-- Create Members table with constraints
CREATE TABLE Members (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    member_id VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    membership_type VARCHAR(20) NOT NULL
);

-- Create indexes for performance
CREATE INDEX idx_member_id ON Members(member_id);
CREATE INDEX idx_member_email ON Members(email);
CREATE INDEX idx_member_status ON Members(status);


-- Verify data
SELECT * FROM Members;
