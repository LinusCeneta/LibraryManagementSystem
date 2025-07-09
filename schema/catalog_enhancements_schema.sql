-- Authors Table
CREATE TABLE Authors (
    AuthorID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    AuthorName VARCHAR(255) NOT NULL UNIQUE,
    Biography CLOB -- Optional
    -- Other author details like birth/death dates, nationality could be added
);

-- Publishers Table
CREATE TABLE Publishers (
    PublisherID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    PublisherName VARCHAR(255) NOT NULL UNIQUE,
    Address VARCHAR(255), -- Optional
    Website VARCHAR(255) -- Optional
);

-- Categories Table (for Genres, Subjects, etc.)
CREATE TABLE Categories (
    CategoryID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    CategoryName VARCHAR(100) NOT NULL UNIQUE,
    ParentCategoryID INT, -- For hierarchical categories (e.g., Fiction -> Sci-Fi)
    Description CLOB, -- Optional
    FOREIGN KEY (ParentCategoryID) REFERENCES Categories(CategoryID) ON DELETE SET NULL
);

-- Locations/Branches Table
CREATE TABLE Locations (
    LocationID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    BranchName VARCHAR(100) NOT NULL UNIQUE,
    Address VARCHAR(255),
    PhoneNumber VARCHAR(20),
    OperatingHours VARCHAR(255) -- Could be more structured if needed
);

-- Books Table (Enhancements/Review)
-- Assuming Books table exists. These are potential ALTER statements or considerations for a new table.
-- For an existing table:
-- ALTER TABLE Books ADD COLUMN Subtitle VARCHAR(255);
-- ALTER TABLE Books ADD COLUMN PublisherID INT;
-- ALTER TABLE Books ADD CONSTRAINT fk_books_publisher FOREIGN KEY (PublisherID) REFERENCES Publishers(PublisherID);
-- ALTER TABLE Books ADD COLUMN Language VARCHAR(10); -- e.g., 'ENG', 'FRA', 'SPA' (ISO 639-1 or 639-2 codes)
-- ALTER TABLE Books ADD COLUMN Format VARCHAR(50); -- e.g., Hardcover, Paperback, eBook, Audiobook, DVD. Consider linking to ItemTypes or a new Formats table.
-- ALTER TABLE Books ADD COLUMN PageCount INT;
-- ALTER TABLE Books ADD COLUMN DeweyDecimal VARCHAR(50);
-- ALTER TABLE Books ADD COLUMN LOCCallNumber VARCHAR(50); -- Library of Congress Call Number
-- ALTER TABLE Books ADD COLUMN Description CLOB; -- For summary/abstract, ensure it's text-searchable if DB supports
-- ALTER TABLE Books ADD COLUMN CoverImageURL VARCHAR(255);
-- ALTER TABLE Books ADD COLUMN CheckoutCount INT DEFAULT 0; -- For popularity tracking
-- ALTER TABLE Books ADD COLUMN HoldCount INT DEFAULT 0; -- For popularity tracking

-- If creating Books table anew (example structure):
/*
CREATE TABLE Books (
    BookID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    ISBN VARCHAR(20) UNIQUE, -- Can be null for older books
    Title VARCHAR(255) NOT NULL,
    Subtitle VARCHAR(255),
    PublisherID INT,
    PublicationYear INT,
    Edition VARCHAR(50),
    Language VARCHAR(10),
    Format VARCHAR(50), -- Or ItemTypeID FK
    PageCount INT,
    Description CLOB,
    CoverImageURL VARCHAR(255),
    DeweyDecimal VARCHAR(50),
    LOCCallNumber VARCHAR(50),
    DateAdded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CheckoutCount INT DEFAULT 0,
    HoldCount INT DEFAULT 0,
    FOREIGN KEY (PublisherID) REFERENCES Publishers(PublisherID)
);
*/

-- BookAuthors Linking Table (Many-to-Many between Books and Authors)
CREATE TABLE BookAuthors (
    BookID INT NOT NULL,
    AuthorID INT NOT NULL,
    PRIMARY KEY (BookID, AuthorID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE,
    FOREIGN KEY (AuthorID) REFERENCES Authors(AuthorID) ON DELETE CASCADE
);

-- BookCategories Linking Table (Many-to-Many between Books and Categories)
CREATE TABLE BookCategories (
    BookID INT NOT NULL,
    CategoryID INT NOT NULL,
    PRIMARY KEY (BookID, CategoryID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE CASCADE
);

-- Copies Table (Enhancements/Review)
-- Assuming Copies table exists.
-- ALTER TABLE Copies ADD COLUMN LocationID INT;
-- ALTER TABLE Copies ADD CONSTRAINT fk_copies_location FOREIGN KEY (LocationID) REFERENCES Locations(LocationID);
-- Ensure 'Status' column exists: Available, Checked Out, On Hold, Lost, Damaged, Under Repair, Withdrawn, In Transit.
-- Ensure 'ItemTypeID' (FK to ItemTypes) exists if 'Format' is not directly on Books table.


-- Keywords/Subjects Table (Optional)
CREATE TABLE Keywords (
    KeywordID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    KeywordText VARCHAR(100) NOT NULL UNIQUE
);

-- BookKeywords Linking Table (Optional, Many-to-Many for Books and Keywords)
CREATE TABLE BookKeywords (
    BookID INT NOT NULL,
    KeywordID INT NOT NULL,
    PRIMARY KEY (BookID, KeywordID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE,
    FOREIGN KEY (KeywordID) REFERENCES Keywords(KeywordID) ON DELETE CASCADE
);

-- BookCollections / CuratedLists Table
CREATE TABLE BookCollections (
    CollectionID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    CollectionName VARCHAR(255) NOT NULL,
    Description CLOB,
    CreatedByStaffID INT NOT NULL, -- FK to Users table (assuming staff are Users)
    DateCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IsPublic BOOLEAN DEFAULT TRUE, -- For public display
    FOREIGN KEY (CreatedByStaffID) REFERENCES Users(UserID)
);

-- BookCollectionItems Linking Table (Many-to-Many for Books and Collections)
CREATE TABLE BookCollectionItems (
    CollectionItemID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    CollectionID INT NOT NULL,
    BookID INT NOT NULL,
    OrderInCollection INT DEFAULT 0, -- For manually ordered lists
    DateAdded TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CollectionID) REFERENCES BookCollections(CollectionID) ON DELETE CASCADE,
    FOREIGN KEY (BookID) REFERENCES Books(BookID) ON DELETE CASCADE,
    UNIQUE (CollectionID, BookID) -- A book can only be in a collection once
);


-- Indexes for Search and Faceting
CREATE INDEX idx_books_title ON Books(Title); -- Standard index, Full-text index might be better
CREATE INDEX idx_books_publicationyear ON Books(PublicationYear);
CREATE INDEX idx_books_language ON Books(Language);
CREATE INDEX idx_books_format ON Books(Format); -- Or on ItemTypes if linked

CREATE INDEX idx_authors_name ON Authors(AuthorName);
CREATE INDEX idx_publishers_name ON Publishers(PublisherName);
CREATE INDEX idx_categories_name ON Categories(CategoryName);
CREATE INDEX idx_keywords_text ON Keywords(KeywordText);

CREATE INDEX idx_bookauthors_authorid ON BookAuthors(AuthorID);
CREATE INDEX idx_bookcategories_categoryid ON BookCategories(CategoryID);
CREATE INDEX idx_bookkeywords_keywordid ON BookKeywords(KeywordID);

CREATE INDEX idx_copies_locationid ON Copies(LocationID);
CREATE INDEX idx_copies_status ON Copies(Status);
-- CREATE INDEX idx_copies_itemtypeid ON Copies(ItemTypeID); -- If ItemTypeID is on Copies

CREATE INDEX idx_bookcollections_name ON BookCollections(CollectionName);
CREATE INDEX idx_bookcollectionitems_collectionid ON BookCollectionItems(CollectionID);

-- Note on Full-Text Search:
-- For robust full-text search on Books.Title, Books.Description, Authors.AuthorName etc.,
-- specific database features should be used (e.g., Apache Derby's built-in text search,
-- or integration with external search engines like Lucene/Solr for larger scale).
-- The schema above provides the base fields. Simple LIKE '%...%' queries can be used initially.

-- Initial Data Examples (Illustrative)
-- INSERT INTO Authors (AuthorName) VALUES ('J.R.R. Tolkien'), ('George Orwell'), ('Jane Austen');
-- INSERT INTO Publishers (PublisherName) VALUES ('Allen & Unwin'), ('Secker & Warburg'), ('T. Egerton');
-- INSERT INTO Categories (CategoryName) VALUES ('Fiction'), ('Science Fiction'), ('Fantasy'), ('Classic Literature'), ('Dystopian');
-- UPDATE Categories SET ParentCategoryID = (SELECT CategoryID FROM Categories WHERE CategoryName='Fiction') WHERE CategoryName='Fantasy';
-- INSERT INTO Locations (BranchName, Address) VALUES ('Main Library', '123 Library St, Bookville'), ('North Branch', '456 North Rd, Readburg');
-- INSERT INTO ItemTypes (TypeName) VALUES ('Hardcover'), ('Paperback'), ('eBook'); -- Assuming ItemTypes table from Circulation schema
-- Then update Books.Format or Copies.ItemTypeID accordingly.
-- INSERT INTO Books (Title, PublisherID, PublicationYear, Format, Language) VALUES ('The Hobbit', (SELECT PublisherID FROM Publishers WHERE PublisherName='Allen & Unwin'), 1937, 'Paperback', 'ENG');
-- INSERT INTO BookAuthors (BookID, AuthorID) VALUES ((SELECT BookID FROM Books WHERE Title='The Hobbit'), (SELECT AuthorID FROM Authors WHERE AuthorName='J.R.R. Tolkien'));
-- INSERT INTO BookCategories (BookID, CategoryID) VALUES ((SELECT BookID FROM Books WHERE Title='The Hobbit'), (SELECT CategoryID FROM Categories WHERE CategoryName='Fantasy'));
-- INSERT INTO Copies (BookID, CopyBarcode, LocationID, Status, ItemTypeID) VALUES ((SELECT BookID FROM Books WHERE Title='The Hobbit'), 'HB001', (SELECT LocationID FROM Locations WHERE BranchName='Main Library'), 'Available', (SELECT ItemTypeID FROM ItemTypes WHERE TypeName='Paperback'));
