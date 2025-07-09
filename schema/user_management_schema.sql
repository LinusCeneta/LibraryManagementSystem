-- Roles Table: Defines the different roles within the system
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    RoleName VARCHAR(50) NOT NULL UNIQUE -- e.g., ROLE_ADMIN, ROLE_STAFF, ROLE_MEMBER
);

-- Users Table: Stores core user information including credentials
CREATE TABLE Users (
    UserID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    Username VARCHAR(50) NOT NULL UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL, -- For storing BCrypt hashed passwords
    Email VARCHAR(100) NOT NULL UNIQUE,
    FirstName VARCHAR(100),
    LastName VARCHAR(100),
    RoleID INT NOT NULL,
    IsActive BOOLEAN DEFAULT TRUE,
    ProfilePhotoURL VARCHAR(255), -- Optional, path or URL to profile photo
    DateRegistered TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LastLoginDate TIMESTAMP,
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);

-- UserProfiles Table (Optional): For additional, less frequently accessed user details
-- This helps keep the Users table leaner, especially if many details are optional or large.
CREATE TABLE UserProfiles (
    ProfileID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    UserID INT NOT NULL UNIQUE, -- One-to-one relationship with Users
    AddressLine1 VARCHAR(255),
    AddressLine2 VARCHAR(255),
    City VARCHAR(100),
    StateProvince VARCHAR(100),
    PostalCode VARCHAR(20),
    Country VARCHAR(100),
    PhoneNumber VARCHAR(20),
    BirthDate DATE,
    EmergencyContactName VARCHAR(100),
    EmergencyContactPhone VARCHAR(20),
    -- Other preferences or details can be added here
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- PasswordResetTokens Table: For "Forgot Password" functionality
CREATE TABLE PasswordResetTokens (
    TokenID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    UserID INT NOT NULL,
    Token VARCHAR(255) NOT NULL UNIQUE, -- The unique reset token
    ExpiryDate TIMESTAMP NOT NULL,
    IsUsed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

-- AuditLog Table (Optional): For logging critical user and system actions
CREATE TABLE AuditLog (
    LogID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    UserID INT, -- Can be null if action is system-initiated or pre-login
    UsernameAttempted VARCHAR(50), -- For logging username in login attempts
    Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Action VARCHAR(100) NOT NULL, -- e.g., LOGIN_SUCCESS, LOGIN_FAILURE, USER_REGISTERED, PW_RESET_REQUEST, PW_RESET_SUCCESS
    IPAddress VARCHAR(45), -- Supports IPv4 and IPv6
    Details CLOB, -- For storing additional JSON or text details about the event
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE SET NULL -- Keep log even if user is deleted
);

-- Initial Data for Roles (Essential for system operation)
-- Run these inserts after creating the tables.
-- INSERT INTO Roles (RoleName) VALUES ('ROLE_ADMIN');
-- INSERT INTO Roles (RoleName) VALUES ('ROLE_STAFF');
-- INSERT INTO Roles (RoleName) VALUES ('ROLE_MEMBER');

-- Indexes for performance
CREATE INDEX idx_users_email ON Users(Email);
CREATE INDEX idx_users_roleid ON Users(RoleID);
CREATE INDEX idx_userprofiles_userid ON UserProfiles(UserID);
CREATE INDEX idx_pwreset_token ON PasswordResetTokens(Token);
CREATE INDEX idx_pwreset_userid ON PasswordResetTokens(UserID);
CREATE INDEX idx_auditlog_userid ON AuditLog(UserID);
CREATE INDEX idx_auditlog_action ON AuditLog(Action);
CREATE INDEX idx_auditlog_timestamp ON AuditLog(Timestamp);

-- Note on Permissions:
-- Actual permissions (e.g., what a ROLE_STAFF can do) are typically enforced
-- in the application logic (Servlet filters, web.xml security constraints, service layer checks)
-- rather than being stored directly as data in a Permissions table, unless a very dynamic
-- permission system is required. For this scope, RoleName implies a set of permissions.
