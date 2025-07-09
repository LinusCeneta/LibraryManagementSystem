-- Suppliers Table
CREATE TABLE Suppliers (
    SupplierID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    SupplierName VARCHAR(255) NOT NULL,
    ContactPerson VARCHAR(255),
    Address VARCHAR(255),
    PhoneNumber VARCHAR(20),
    Email VARCHAR(255) UNIQUE,
    PaymentTerms VARCHAR(100)
);

-- AcquisitionRequests Table
CREATE TABLE AcquisitionRequests (
    RequestID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    BookTitle VARCHAR(255) NOT NULL,
    Author VARCHAR(255),
    ISBN VARCHAR(20),
    Publisher VARCHAR(255),
    PublicationYear INT,
    RequestedBy INT NOT NULL, -- Foreign Key to Members or Users table
    RequestDate DATE NOT NULL DEFAULT CURRENT_DATE,
    Status VARCHAR(50) DEFAULT 'Pending', -- Pending, Approved, Rejected, Ordered
    Notes CLOB,
    FOREIGN KEY (RequestedBy) REFERENCES Users(UserID) -- Assuming a Users table with UserID
);

-- PurchaseOrders Table
CREATE TABLE PurchaseOrders (
    PO_ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    PONumber VARCHAR(50) UNIQUE NOT NULL,
    SupplierID INT NOT NULL,
    OrderDate DATE NOT NULL DEFAULT CURRENT_DATE,
    ExpectedDeliveryDate DATE,
    Status VARCHAR(50) DEFAULT 'Created', -- Created, Submitted, Partially Received, Fully Received, Closed
    TotalAmount DECIMAL(10, 2) DEFAULT 0.00,
    CreatedBy INT NOT NULL, -- Foreign Key to Users table (staff who created the PO)
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID) -- Assuming a Users table
);

-- PurchaseOrderLines Table
-- Links PurchaseOrders to Books (or a temporary holding table if book doesn't exist yet)
CREATE TABLE PurchaseOrderLines (
    POLineID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    PO_ID INT NOT NULL,
    BookID INT, -- Foreign Key to Books table (if book already exists in catalog)
    RequestedBookTitle VARCHAR(255), -- Used if BookID is null (new book)
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10, 2) NOT NULL,
    LineTotal AS (Quantity * UnitPrice),
    FOREIGN KEY (PO_ID) REFERENCES PurchaseOrders(PO_ID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID) -- Assuming a Books table with BookID
);

-- GoodsReceiptNotes Table (Receiving)
CREATE TABLE GoodsReceiptNotes (
    GRN_ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    PO_ID INT NOT NULL,
    SupplierID INT NOT NULL,
    InvoiceNumber VARCHAR(100),
    InvoiceDate DATE,
    ReceivedDate DATE NOT NULL DEFAULT CURRENT_DATE,
    ReceivedBy INT NOT NULL, -- Foreign Key to Users table (staff who received)
    Notes CLOB, -- For discrepancies, damages, etc.
    FOREIGN KEY (PO_ID) REFERENCES PurchaseOrders(PO_ID),
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
    FOREIGN KEY (ReceivedBy) REFERENCES Users(UserID) -- Assuming a Users table
);

-- GoodsReceiptNoteItems Table
-- Details of items received in a GRN, linking back to PO Lines and updating Copies
CREATE TABLE GoodsReceiptNoteItems (
    GRNItemID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    GRN_ID INT NOT NULL,
    POLineID INT, -- Link to the specific line item in the PO
    BookID INT, -- From POLine or directly if ad-hoc receipt
    ReceivedQuantity INT NOT NULL,
    AcceptedQuantity INT NOT NULL, -- Quantity accepted after inspection
    Condition VARCHAR(50) DEFAULT 'New', -- New, Good, Fair, Damaged
    Notes VARCHAR(255), -- e.g., "1 copy damaged"
    FOREIGN KEY (GRN_ID) REFERENCES GoodsReceiptNotes(GRN_ID),
    FOREIGN KEY (POLineID) REFERENCES PurchaseOrderLines(POLineID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
    -- When an item is received and accepted, a new record in the 'Copies' table should be created.
    -- This table helps track what was received against what was ordered.
);

-- InventoryAdjustments Table
CREATE TABLE InventoryAdjustments (
    AdjustmentID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    CopyID INT NOT NULL, -- Foreign Key to Copies table
    AdjustmentDate DATE NOT NULL DEFAULT CURRENT_DATE,
    Reason VARCHAR(255) NOT NULL, -- e.g., Lost, Damaged, Found, Initial Stock, Cycle Count Adjustment
    AdjustedBy INT NOT NULL, -- Foreign Key to Users table (staff)
    QuantityChange INT NOT NULL, -- e.g., -1 for lost, +1 for found
    Notes CLOB,
    FOREIGN KEY (CopyID) REFERENCES Copies(CopyID), -- Assuming a Copies table with CopyID
    FOREIGN KEY (AdjustedBy) REFERENCES Users(UserID) -- Assuming a Users table
);

-- Note: This schema assumes the existence of Users, Books, and Copies tables.
-- For Books table, it's assumed to have at least BookID, Title, Author, ISBN.
-- For Copies table, it's assumed to have CopyID, BookID, Status, Location.
-- UserID in Users table is assumed to be the primary key for staff/admin users.
-- MemberID in a Members table would be used if members can request acquisitions directly.
-- For simplicity in AcquisitionRequests, RequestedBy refers to UserID; adapt if Members have separate IDs.

-- Indexes for performance
CREATE INDEX idx_po_supplier ON PurchaseOrders(SupplierID);
CREATE INDEX idx_po_status ON PurchaseOrders(Status);
CREATE INDEX idx_poline_po ON PurchaseOrderLines(PO_ID);
CREATE INDEX idx_poline_book ON PurchaseOrderLines(BookID);
CREATE INDEX idx_grn_po ON GoodsReceiptNotes(PO_ID);
CREATE INDEX idx_grnitem_grn ON GoodsReceiptNoteItems(GRN_ID);
CREATE INDEX idx_invadj_copy ON InventoryAdjustments(CopyID);
CREATE INDEX idx_acqreq_status ON AcquisitionRequests(Status);
