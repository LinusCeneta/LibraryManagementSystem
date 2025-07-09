-- MembershipTiers Table: Defines different membership levels and their borrowing rules
CREATE TABLE MembershipTiers (
    MembershipTierID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    TierName VARCHAR(100) NOT NULL UNIQUE,
    BorrowingLimit INT NOT NULL DEFAULT 5, -- Max number of items that can be borrowed simultaneously
    LoanDurationDays INT NOT NULL DEFAULT 14, -- Standard loan period in days
    RenewalLimit INT NOT NULL DEFAULT 2 -- Max number of times a loan can be renewed
    -- Other tier-specific rules can be added here
);

-- Update Users Table (or a dedicated Members table if you have one)
-- Add MembershipTierID and MembershipStatus
-- This assumes you are extending the existing Users table.
-- If you have a separate Members table, apply these to that table.
-- ALTER TABLE Users ADD COLUMN MembershipTierID INT;
-- ALTER TABLE Users ADD CONSTRAINT fk_users_membershiptier FOREIGN KEY (MembershipTierID) REFERENCES MembershipTiers(MembershipTierID);
-- ALTER TABLE Users ADD COLUMN MembershipStatus VARCHAR(50) DEFAULT 'Active'; -- Active, Suspended, Expired, Cancelled
-- ALTER TABLE Users ADD COLUMN CurrentFineBalance DECIMAL(10, 2) DEFAULT 0.00;

-- ItemTypes Table (Optional, if fine policies vary significantly by item type)
CREATE TABLE ItemTypes (
    ItemTypeID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    TypeName VARCHAR(50) NOT NULL UNIQUE, -- e.g., Book, DVD, Magazine, Reference
    IsLoanable BOOLEAN DEFAULT TRUE -- Reference items might not be loanable
);

-- Update Copies Table
-- Add ItemTypeID, CurrentLoanID
-- ALTER TABLE Copies ADD COLUMN ItemTypeID INT;
-- ALTER TABLE Copies ADD CONSTRAINT fk_copies_itemtype FOREIGN KEY (ItemTypeID) REFERENCES ItemTypes(ItemTypeID);
-- ALTER TABLE Copies ADD COLUMN CurrentLoanID INT; -- FK to Loans table, added later after Loans is created
-- The 'Status' column (Available, Checked Out, etc.) is assumed to already exist or be added.
-- Example: ALTER TABLE Copies ADD COLUMN Status VARCHAR(50) DEFAULT 'Available';

-- Loans Table: Records all borrowing transactions
CREATE TABLE Loans (
    LoanID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    MemberID INT NOT NULL, -- FK to Users (or Members table)
    CopyID INT NOT NULL UNIQUE, -- A copy can only be out on one loan at a time (if active)
                                -- To allow history, this UNIQUE might be on (CopyID, ReturnDate IS NULL) if DB supports partial indexes
                                -- Or handle in application logic. For simplicity, assuming CopyID is unique for active loans.
    StaffID INT NOT NULL, -- FK to Users (staff who processed the loan)
    IssueDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DueDate TIMESTAMP NOT NULL,
    ReturnDate TIMESTAMP, -- Null if item is still on loan
    LoanStatus VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, RETURNED, OVERDUE, LOST_ITEM
    RenewalsDone INT DEFAULT 0, -- Number of times this loan has been renewed
    FOREIGN KEY (MemberID) REFERENCES Users(UserID), -- Assuming Members are in Users table
    FOREIGN KEY (CopyID) REFERENCES Copies(CopyID),
    FOREIGN KEY (StaffID) REFERENCES Users(UserID) -- Staff are also in Users table
);

-- Add FK from Copies to Loans after Loans table is created
-- ALTER TABLE Copies ADD CONSTRAINT fk_copies_currentloan FOREIGN KEY (CurrentLoanID) REFERENCES Loans(LoanID) ON DELETE SET NULL;


-- FinePolicies Table: Defines rules for calculating fines
CREATE TABLE FinePolicies (
    PolicyID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    ItemTypeID INT, -- FK to ItemTypes, null if a general policy
    PolicyName VARCHAR(100) NOT NULL UNIQUE,
    OverdueFinePerDay DECIMAL(5, 2) NOT NULL DEFAULT 0.25,
    MaxFineAmount DECIMAL(10, 2), -- Max overdue fine per loan item, null if no max
    LostItemFeeFixed DECIMAL(10,2), -- A fixed fee for lost items
    LostItemFeePercentage DECIMAL(5,2), -- Percentage of item's cost for lost items (e.g., 1.00 for 100%)
                                        -- Use one of Fixed or Percentage, or define precedence in app logic
    GracePeriodDays INT DEFAULT 0, -- Number of days after due date before fines start accruing
    FOREIGN KEY (ItemTypeID) REFERENCES ItemTypes(ItemTypeID)
);

-- Fines Table: Records fines accrued by members
CREATE TABLE Fines (
    FineID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    LoanID BIGINT, -- FK to Loans, can be null if fine is not related to a specific loan (e.g., manual damage fee)
    MemberID INT NOT NULL, -- FK to Users (or Members table)
    FineAmount DECIMAL(10, 2) NOT NULL,
    AmountPaid DECIMAL(10, 2) DEFAULT 0.00,
    DateLevied TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DatePaid TIMESTAMP, -- Null if not fully paid
    FineStatus VARCHAR(50) NOT NULL DEFAULT 'UNPAID', -- UNPAID, PAID, PARTIALLY_PAID, WAIVED
    Reason VARCHAR(255), -- e.g., OVERDUE, DAMAGED_ITEM, LOST_ITEM, MEMBERSHIP_FEE
    Notes CLOB, -- Additional details by staff
    ProcessedByStaffID INT, -- Staff who processed payment or waiver
    FOREIGN KEY (LoanID) REFERENCES Loans(LoanID) ON DELETE SET NULL,
    FOREIGN KEY (MemberID) REFERENCES Users(UserID),
    FOREIGN KEY (ProcessedByStaffID) REFERENCES Users(UserID)
);

-- Holds Table: Manages member requests for items currently unavailable
CREATE TABLE Holds (
    HoldID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    BookID INT NOT NULL, -- FK to Books table (holds are on a bibliographic record)
    MemberID INT NOT NULL, -- FK to Users (or Members table)
    RequestDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, READY_FOR_PICKUP, FULFILLED, CANCELLED, EXPIRED
    ExpiryDate TIMESTAMP, -- Date when a 'READY_FOR_PICKUP' hold expires if not collected
    FulfilledByCopyID INT, -- FK to Copies, set when a specific copy is assigned for pickup
    PickupLocationID INT, -- FK to a Locations/Branches table (if multi-branch)
    QueuePosition INT, -- Calculated or managed by application logic if strict ordering needed beyond RequestDate
    NotificationSentDate TIMESTAMP, -- When member was notified it's ready for pickup
    FOREIGN KEY (BookID) REFERENCES Books(BookID),
    FOREIGN KEY (MemberID) REFERENCES Users(UserID),
    FOREIGN KEY (FulfilledByCopyID) REFERENCES Copies(CopyID)
    -- FOREIGN KEY (PickupLocationID) REFERENCES Locations(LocationID) -- If you have a Locations table
);


-- Initial Data for MembershipTiers (Example)
-- INSERT INTO MembershipTiers (TierName, BorrowingLimit, LoanDurationDays, RenewalLimit) VALUES ('Adult Regular', 10, 21, 2);
-- INSERT INTO MembershipTiers (TierName, BorrowingLimit, LoanDurationDays, RenewalLimit) VALUES ('Student', 15, 28, 3);
-- INSERT INTO MembershipTiers (TierName, BorrowingLimit, LoanDurationDays, RenewalLimit) VALUES ('Senior', 8, 28, 2);
-- INSERT INTO MembershipTiers (TierName, BorrowingLimit, LoanDurationDays, RenewalLimit) VALUES ('Child', 5, 14, 1);
-- INSERT INTO MembershipTiers (TierName, BorrowingLimit, LoanDurationDays, RenewalLimit) VALUES ('Staff Internal', 20, 60, 5);


-- Initial Data for ItemTypes (Example)
-- INSERT INTO ItemTypes (TypeName, IsLoanable) VALUES ('General Book', TRUE);
-- INSERT INTO ItemTypes (TypeName, IsLoanable) VALUES ('DVD/Blu-ray', TRUE);
-- INSERT INTO ItemTypes (TypeName, IsLoanable) VALUES ('Magazine', TRUE);
-- INSERT INTO ItemTypes (TypeName, IsLoanable) VALUES ('Reference Material', FALSE);
-- INSERT INTO ItemTypes (TypeName, IsLoanable) VALUES ('Childrens Book', TRUE);

-- Initial Data for FinePolicies (Example)
-- INSERT INTO FinePolicies (PolicyName, OverdueFinePerDay, MaxFineAmount, LostItemFeePercentage, GracePeriodDays) VALUES ('Default Book Policy', 0.25, 10.00, 1.0, 2);
-- INSERT INTO FinePolicies (ItemTypeID, PolicyName, OverdueFinePerDay, MaxFineAmount, LostItemFeePercentage, GracePeriodDays)
--     SELECT ItemTypeID, 'DVD Policy', 1.00, 15.00, 1.0, 0 FROM ItemTypes WHERE TypeName = 'DVD/Blu-ray';


-- Indexes for performance
CREATE INDEX idx_loans_memberid ON Loans(MemberID);
CREATE INDEX idx_loans_copyid ON Loans(CopyID);
CREATE INDEX idx_loans_duedate ON Loans(DueDate);
CREATE INDEX idx_loans_status ON Loans(LoanStatus);

CREATE INDEX idx_fines_loanid ON Fines(LoanID);
CREATE INDEX idx_fines_memberid ON Fines(MemberID);
CREATE INDEX idx_fines_status ON Fines(FineStatus);

CREATE INDEX idx_holds_bookid ON Holds(BookID);
CREATE INDEX idx_holds_memberid ON Holds(MemberID);
CREATE INDEX idx_holds_status ON Holds(Status);

-- Add necessary ALTER TABLE statements here to modify Users and Copies
-- These are commented out as they depend on the exact current state of your Users/Copies tables.
-- Ensure you run them after backing up your DB if it has data.

-- Example ALTER statements (ensure column names/types match your existing schema if Users/Copies exist):
-- Make sure these are compatible with your existing Users table (e.g. if it's for members only or all user types)
-- ALTER TABLE Users ADD COLUMN MembershipTierID INT DEFAULT NULL; -- Default to NULL or a base tier ID
-- ALTER TABLE Users ADD CONSTRAINT fk_users_tier FOREIGN KEY (MembershipTierID) REFERENCES MembershipTiers(MembershipTierID);
-- ALTER TABLE Users ADD COLUMN MembershipStatus VARCHAR(50) DEFAULT 'Active' NOT NULL;
-- ALTER TABLE Users ADD COLUMN CurrentFineBalance DECIMAL(10,2) DEFAULT 0.00 NOT NULL;

-- For Copies Table
-- ALTER TABLE Copies ADD COLUMN ItemTypeID INT DEFAULT NULL; -- Default to a general item type ID
-- ALTER TABLE Copies ADD CONSTRAINT fk_copies_itemtype FOREIGN KEY (ItemTypeID) REFERENCES ItemTypes(ItemTypeID);
-- ALTER TABLE Copies ADD COLUMN CurrentLoanID BIGINT DEFAULT NULL;
-- ALTER TABLE Copies ADD CONSTRAINT fk_copies_loan FOREIGN KEY (CurrentLoanID) REFERENCES Loans(LoanID) ON DELETE SET NULL;
-- If Status column doesn't exist in Copies:
-- ALTER TABLE Copies ADD COLUMN Status VARCHAR(50) DEFAULT 'Available' NOT NULL;
-- Assumed values for Copy Status: 'Available', 'Checked Out', 'On Hold', 'Lost', 'Damaged', 'Under Repair', 'Withdrawn', 'In Transit'
