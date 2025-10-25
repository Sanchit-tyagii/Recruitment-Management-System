# Recruitment-Management-System
A Spring Boot 3 backend with Spring Security and JWT authentication to manage users, applicants, jobs, and resumes. Admins can post jobs and manage applicants; applicants can upload resumes, apply for jobs, and view listings. Integrated with a Resume Parsing API for automated data extraction.
# Recruitment Management System (Java Spring Boot)

A fully functional backend server for managing recruitment workflows including user authentication, resume parsing, and job applications using **Spring Boot**, **JWT**, and **MongoDB**.

## 📘 Project Overview

This project provides APIs for:
- User registration and authentication (Admin & Applicant)
- Resume upload and parsing via third-party API (APIlayer)
- Job posting and listing
- Applicant profile management and job applications

---

## 🏗️ Tech Stack

- **Language:** Java 17  
- **Framework:** Spring Boot 3.3  
- **Security:** Spring Security with JWT  
- **Database:** MongoDB  
- **API Client:** Spring WebClient  
- **Build Tool:** Maven  

---

## ⚙️ Features

### **Authentication**
- Secure signup and login endpoints using JWT.
- Passwords encrypted with BCrypt.
- Role-based access control (Admin/Applicant).

### **Applicant**
- Can upload resumes (PDF or DOCX only).
- Resume parsed through third-party API (apilayer.com) and stored in profile.
- Can view job listings and apply to jobs.

### **Admin**
- Can create job openings.
- Can view all applicant profiles and uploaded resumes.
- Can fetch detailed job data including applicants and application counts.

---

## 🔐 JWT Authentication Flow

1. User logs in via `/login` and receives a JWT token.  
2. Token is passed in `Authorization: Bearer <token>` header.  
3. The `JwtFilter` validates the token and sets authentication context.  
4. Access to protected endpoints is controlled via roles.

---

## 🗂️ API Endpoints

### **Authentication**
| Method | Endpoint | Access | Description |
|--------|-----------|--------|--------------|
| POST | `/signup` | Public | Register new Admin or Applicant user |
| POST | `/login` | Public | Authenticate user and return JWT |

---

### **Resume Management**
| Method | Endpoint | Access | Description |
|--------|-----------|--------|--------------|
| POST | `/uploadResume` | Applicant | Upload and parse resume using APIlayer |
| - | - | - | Only `.pdf` or `.docx` allowed |

**Third-party API:**
curl --location --request POST 'https://api.apilayer.com/resume_parser/upload'
--header 'Content-Type: application/octet-stream'
--header 'apikey: R4nD0mK3y_123ABC456XYZ'
--data-binary '@/path/to/resume.docx'


---

### **Job Management**
| Method | Endpoint | Access | Description |
|--------|-----------|--------|--------------|
| POST | `/admin/job` | Admin | Post a new job |
| GET | `/admin/job/{jobId}` | Admin | Get job details and applicant list |
| GET | `/jobs` | Applicant/Admin | View all job openings |
| GET | `/jobs/apply?job_id={id}` | Applicant | Apply for a job |

Application counts increment automatically using transactional JPA logic.

---

### **Applicant Profile**
| Method | Endpoint | Access | Description |
|--------|-----------|--------|--------------|
| GET | `/admin/applicants` | Admin | List all applicants |
| GET | `/admin/applicant/{id}` | Admin | Fetch applicant’s detailed parsed profile |

---

## 🧩 Key Classes

### **JwtFilter**
Validates JWT from request headers and sets user in Spring’s `SecurityContextHolder`.

### **JwtUtil**
Handles token generation, validation, signature verification, and subject decoding.

### **AuthService**
Provides signup and login functionality with password hashing and token issuance.

### **JobService**
Manages job creation, listing, and application increments.

### **ResumeService**
Handles file upload, calls external resume parsing API, and updates `Profile` entity.

### **UserService**
Fetches user and applicant data for admin endpoints.

---

## 🧪 Example Response from Resume Parser

{
"name": "Elon Musk",
"email": "elonmusk@teslamotors.com",
"phone": "650 68100",
"skills": ["Entrepreneurship", "Innovation", "Physics"],
"education": [
{"name": "Wharton School"},
{"name": "University of Pennsylvania"}
],
"experience": [
{"name": "SpaceX"},
{"name": "Tesla Motors"}
]
}

---

## 🚀 Setup Instructions

1. Clone the repository:
git clone using this link : https://github.com/Sanchit-tyagii/Recruitment-Management-System
2. Add environment variables in `application.yml`:
jwt.secret=YourJWTSecretHere
jwt.expirationMs=86400000
resume-parser.url=https://api.apilayer.com/resume_parser/upload
resume-parser.apikey=YourAPIKeyHere
file.upload-dir=/uploads/resumes
3. Start MongoDB locally or see how to use driver or via Docker.
5. Run the application:

5. Test APIs in Postman using JWT headers.

---

## 📊 Database Structure

**User Collection**
- id, name, email, address, passwordHash, userType, profileHeadline

**Profile Collection**
- userId, resumeFilePath, skills, education, experience, phone, email, name

**Job Collection**
- id, title, description, companyName, postedBy, postedOn, totalApplications

---

## ⚠️ Error Handling
- Rate limit errors handled gracefully when the APIParser quota is exceeded.
- Validation for file formats and duplicate users.

---

## 🏁 License
MIT License © 2025 `Sanchit Tyagi`

---

## 👨‍💻 Author
Developed by `Sanchit Tyagi`  
Contact: `sanchitbijna@gmail.com`  

