# Medical Records System — Full Project Documentation

This document explains every file, every function, and every annotation in plain English.
No Java knowledge is assumed.

---

## Table of Contents

1. [What the project does](#1-what-the-project-does)
2. [How the layers fit together](#2-how-the-layers-fit-together)
3. [Annotations (tags) dictionary](#3-annotations-tags-dictionary)
4. [Models — the data structures](#4-models--the-data-structures)
5. [Repositories — talking to the database](#5-repositories--talking-to-the-database)
6. [Services — the business logic](#6-services--the-business-logic)
7. [Controllers — the web pages](#7-controllers--the-web-pages)
8. [API Controllers — the REST API](#8-api-controllers--the-rest-api)
9. [Config — setup and security](#9-config--setup-and-security)
10. [Exceptions — error handling](#10-exceptions--error-handling)
11. [Tests](#11-tests)
12. [Main entry point](#12-main-entry-point)

---

## 1. What the project does

This is a **medical records web application**. It tracks:

- **Doctors** — their name, specialty, and whether they can be a General Practitioner (GP)
- **Patients** — their name, national ID (EGN), their personal GP, and whether they have health insurance
- **Diagnoses** — medical codes and descriptions (e.g. "J06 — Upper respiratory infection")
- **Examinations** — a visit record linking a doctor, a patient, and a diagnosis, with a price and treatment notes
- **Sick Leaves** — documents issued after an examination, recording how many days off a patient gets
- **Users** — login accounts linked to a doctor or patient, with a role (ADMIN, DOCTOR, or PATIENT)

There are **three types of users**:
- `ADMIN` — can do everything
- `DOCTOR` — can manage examinations and sick leaves, but only their own
- `PATIENT` — can only view their own records

The system has **two interfaces**:
1. A **web UI** (HTML pages you open in a browser) at `http://localhost:8080`
2. A **REST API** (for other programs to talk to) at `http://localhost:8080/api/v1/`

---

## 2. How the layers fit together

The code is split into layers. A request from a user flows like this:

```
Browser / API client
        ↓
   Controller  (receives the request, decides what to do)
        ↓
    Service    (contains the rules and logic)
        ↓
  Repository  (reads/writes to the database)
        ↓
   Database   (PostgreSQL — stores everything permanently)
```

- **Models** define the shape of the data (what a Doctor or Patient looks like).
- **Repositories** are the bridge to the database — they know how to find, save, and delete records.
- **Services** contain the business rules — e.g. "you cannot save a patient with a duplicate EGN".
- **Controllers** receive web requests and call the service to get results.
- **Config** sets up security (who can log in, who can access what).
- **Exceptions** handle errors gracefully so the app doesn't crash.

---

## 3. Annotations (tags) dictionary

Annotations in Java start with `@`. They are instructions to the framework about what a class or method should do.

| Annotation | What it means in plain English |
|---|---|
| `@Entity` | "This class represents a table in the database." |
| `@Table(name="x")` | "The database table is called x." |
| `@Id` | "This field is the unique identifier (primary key) for each row." |
| `@GeneratedValue` | "The database will automatically assign a number to this field (auto-increment)." |
| `@Column` | "This field maps to a column in the database table." |
| `@Column(unique=true)` | "No two rows can have the same value in this column." |
| `@Column(nullable=false)` | "This column cannot be empty — it must always have a value." |
| `@ManyToOne` | "Many of these can belong to one of the other thing." (e.g. many patients can have one doctor) |
| `@OneToOne` | "Exactly one of these belongs to exactly one of the other." (e.g. one user per doctor) |
| `@JoinColumn(name="x")` | "Store the relationship using a column called x in the database." |
| `@FetchType.EAGER` | "When loading this record, immediately also load the related record." |
| `@NotBlank` | "This field must not be empty." |
| `@NotNull` | "This field must not be missing entirely." |
| `@Pattern(regexp="...")` | "This field must match this pattern (e.g. exactly 10 digits)." |
| `@Min(value=1)` | "This number must be at least 1." |
| `@DecimalMin("0.0")` | "This decimal number must be at least 0." |
| `@PrePersist` / `@PreUpdate` | "Run this method automatically before saving or updating a record." |
| `@Service` | "This class contains business logic. Spring manages it automatically." |
| `@Repository` | "This class talks to the database. Spring manages it automatically." |
| `@Controller` | "This class handles web page requests and returns HTML." |
| `@RestController` | "This class handles API requests and returns JSON." |
| `@RequestMapping("/x")` | "All URLs in this class start with /x." |
| `@GetMapping` | "This method handles GET requests (opening a page or fetching data)." |
| `@PostMapping` | "This method handles POST requests (submitting a form or creating data)." |
| `@PutMapping` | "This method handles PUT requests (updating data via API)." |
| `@DeleteMapping` | "This method handles DELETE requests (deleting data via API)." |
| `@PathVariable` | "Take this value from the URL — e.g. /doctors/{id} gives us the id." |
| `@RequestParam` | "Take this value from the query string — e.g. ?doctorId=5." |
| `@RequestBody` | "Take this value from the JSON body of the request." |
| `@ModelAttribute` | "Take this value from a submitted HTML form." |
| `@Valid` | "Check the validation rules on this object before proceeding." |
| `@ResponseStatus(HttpStatus.X)` | "Send back HTTP status code X (e.g. 201 Created, 404 Not Found)." |
| `@PreAuthorize("hasRole('X')")` | "Only allow this if the logged-in user has role X." |
| `@RequiredArgsConstructor` | "Automatically create a constructor that takes all the required fields. (Lombok shortcut)" |
| `@Data` | "Automatically create getters, setters, equals, and toString. (Lombok shortcut)" |
| `@NoArgsConstructor` | "Automatically create an empty constructor. (Lombok shortcut)" |
| `@Component` | "This is a general Spring-managed helper class." |
| `@Configuration` | "This class contains setup/configuration code." |
| `@Bean` | "This method creates an object that Spring will manage and share." |
| `@Order(n)` | "Run this before others — lower number = higher priority." |
| `@Transactional` | "Everything in this method happens as one database transaction — if anything fails, all changes are rolled back." |
| `@Transactional(readOnly=true)` | "This only reads from the database, never writes." |
| `@Query("...")` | "Use this exact database query instead of the auto-generated one." |
| `@Param("x")` | "This method parameter maps to :x in the @Query above." |
| `@ControllerAdvice` | "This class handles errors for web page controllers." |
| `@RestControllerAdvice` | "This class handles errors for API controllers and returns JSON." |
| `@ExceptionHandler(X.class)` | "When error X occurs, run this method." |
| `@SpringBootApplication` | "This is the main starting point of the whole application." |
| `@ActiveProfiles("test")` | "Use the test configuration (H2 in-memory database) for this test." |
| `@SpringBootTest` | "Load the full application for this test." |
| `@DataJpaTest` | "Load only the database layer for this test." |
| `@ExtendWith(MockitoExtension.class)` | "Use Mockito (fake objects) for this test." |
| `@Mock` | "Create a fake version of this class for testing." |
| `@InjectMocks` | "Create a real instance of this class but inject the fakes into it." |
| `@Test` | "This method is a test — run it and check if it passes." |
| `@BeforeEach` | "Run this method before each individual test." |

---

## 4. Models — the data structures

Models are classes that represent a row in the database. Each field is a column.

---

### `model/Role.java`

```java
public enum Role {
    ADMIN,
    DOCTOR,
    PATIENT
}
```

An **enum** is a fixed list of named options. This one defines the three possible user roles.
- `ADMIN` — full access
- `DOCTOR` — limited access (own records only)
- `PATIENT` — read-only access to their own records

---

### `model/Doctor.java`

Represents a doctor in the system. Maps to the `doctors` table in the database.

| Field | Type | Meaning |
|---|---|---|
| `id` | Long (number) | Unique auto-assigned ID for each doctor |
| `identificationNumber` | String (text) | A unique code like "GP001" or "CAR002" |
| `name` | String | Full name e.g. "Dr. Ivan Petrov" |
| `specialty` | String | e.g. "General Practice", "Cardiology" |
| `canBeGp` | boolean | true if this doctor can be a personal GP for patients |

**Annotations on fields:**
- `@Id` + `@GeneratedValue` on `id` — the database automatically assigns 1, 2, 3... as IDs
- `@Column(unique=true, nullable=false)` on `identificationNumber` — every doctor must have one and no two doctors can share it
- `@NotBlank` on `name` and `specialty` — these cannot be submitted as empty

**Constructors:** There are two versions of "how to create a Doctor":
1. With an identification number already known — `new Doctor("GP001", "Dr. Ivan", "General Practice", true)`
2. Without — `new Doctor("Dr. Ivan", "General Practice", true)` — the system will generate the ID number later

---

### `model/Patient.java`

Represents a patient. Maps to the `patients` table.

| Field | Type | Meaning |
|---|---|---|
| `id` | Long | Unique auto-assigned ID |
| `name` | String | Patient's full name |
| `egn` | String | Bulgarian national ID — exactly 10 digits, unique |
| `personalDoctor` | Doctor | The GP assigned to this patient (a link to a Doctor record) |
| `healthInsured` | boolean | true if the patient has paid health insurance for the last 6 months |

**Key annotation:**
- `@ManyToOne` on `personalDoctor` — many patients can share the same GP
- `@Pattern(regexp = "\\d{10}")` on `egn` — validates that EGN is exactly 10 digits

---

### `model/Diagnosis.java`

Represents a medical diagnosis code. Maps to the `diagnoses` table.

| Field | Type | Meaning |
|---|---|---|
| `id` | Long | Unique auto-assigned ID |
| `code` | String | Standard medical code e.g. "J06" |
| `description` | String | Human-readable name e.g. "Acute upper respiratory infection" |

Both `code` and `description` are required (`@NotBlank`) and `code` must be unique.

---

### `model/Examination.java`

Represents a single doctor visit / examination. Maps to the `examinations` table.

| Field | Type | Meaning |
|---|---|---|
| `id` | Long | Unique auto-assigned ID |
| `date` | LocalDate | The date of the examination |
| `doctor` | Doctor | Which doctor performed it |
| `patient` | Patient | Which patient attended |
| `diagnosis` | Diagnosis | What was diagnosed |
| `prescribedTreatment` | String | What treatment was prescribed (optional) |
| `price` | BigDecimal | The cost of the examination |
| `paidByNhif` | boolean | true if the national health fund pays; false if the patient pays themselves |

**Key behaviour — `@PrePersist` / `@PreUpdate` on `updatePaymentStatus()`:**
Before every save or update, the system automatically checks if the patient has insurance.
If yes, `paidByNhif` is set to `true`. If no, it's `false`.
This means you never set `paidByNhif` manually — it is always calculated automatically.

---

### `model/SickLeave.java`

Represents a sick leave document issued after an examination. Maps to `sick_leaves`.

| Field | Type | Meaning |
|---|---|---|
| `id` | Long | Unique auto-assigned ID |
| `examination` | Examination | The examination that triggered this sick leave |
| `startDate` | LocalDate | First day of sick leave |
| `numberOfDays` | int | How many days the sick leave lasts |

`numberOfDays` must be at least 1 (`@Min(value=1)`).

---

### `model/User.java`

Represents a login account. Maps to the `users` table.

| Field | Type | Meaning |
|---|---|---|
| `id` | Long | Unique auto-assigned ID |
| `username` | String | Login name — must be unique |
| `password` | String | Stored as a hashed (encrypted) value, never plain text |
| `role` | Role | ADMIN, DOCTOR, or PATIENT |
| `doctor` | Doctor | If this user is a doctor, links to their Doctor record |
| `patient` | Patient | If this user is a patient, links to their Patient record |

A user can be linked to either a doctor or a patient, not both (and admin has neither).

---

## 5. Repositories — talking to the database

Repositories are interfaces (not full classes — Spring generates the code automatically).
They extend `JpaRepository<Model, Long>` which gives them basic operations for free:
- `findAll()` — get all records
- `findById(id)` — get one record by ID
- `save(object)` — create or update a record
- `deleteById(id)` — delete a record
- `existsById(id)` — check if a record exists
- `count()` — count all records

Any method you add with a name following Spring's naming convention is also auto-generated.
For example, `findByEgn(String egn)` means "SELECT * FROM patients WHERE egn = ?".

Custom queries that are too complex for the naming convention use `@Query("...")`.

---

### `repository/DoctorRepository.java`

| Method | What it does |
|---|---|
| `findByIdentificationNumber(String number)` | Find a doctor by their ID code. Returns an Optional (might be empty if not found). |
| `findByCanBeGpTrue()` | Get all doctors where canBeGp = true (all GPs). |
| `existsByIdentificationNumber(String number)` | Returns true/false — does a doctor with this number exist? |
| `countByIdentificationNumberStartingWith(String prefix)` | Count how many doctors have an ID starting with e.g. "GP". Used to generate the next number. |

---

### `repository/PatientRepository.java`

| Method | What it does |
|---|---|
| `findByEgn(String egn)` | Find a patient by their national ID. |
| `findByPersonalDoctor(Doctor doctor)` | Get all patients assigned to a specific doctor. |
| `countByPersonalDoctor(Doctor doctor)` | Count how many patients a doctor has. |
| `existsByEgn(String egn)` | Check if a patient with this EGN already exists. |

---

### `repository/DiagnosisRepository.java`

| Method | What it does |
|---|---|
| `findByCode(String code)` | Find a diagnosis by its code e.g. "J06". |
| `existsByCode(String code)` | Check if a diagnosis code already exists. |

---

### `repository/ExaminationRepository.java`

| Method | What it does |
|---|---|
| `findByPatient(Patient patient)` | All examinations for a specific patient. |
| `findByDoctor(Doctor doctor)` | All examinations performed by a specific doctor. |
| `findByDiagnosis(Diagnosis diagnosis)` | All examinations with a specific diagnosis. |
| `findByDoctorAndDateBetween(doctor, from, to)` | Examinations by a doctor within a date range. |
| `findByDateBetween(from, to)` | All examinations within a date range. |
| `findPatientHistory(Patient patient)` | All examinations for a patient, newest first. Uses a custom `@Query`. |
| `sumPatientPaidExaminations()` | Total money paid by uninsured patients (where paidByNhif = false). Custom `@Query`. |
| `sumPatientPaidByDoctor(Doctor doctor)` | Same but only for a specific doctor's patients. Custom `@Query`. |
| `countByDoctor(Doctor doctor)` | How many examinations a doctor has performed total. Custom `@Query`. |
| `findDiagnosisFrequency()` | Returns diagnoses ordered by how often they appear, most common first. Custom `@Query`. |

---

### `repository/SickLeaveRepository.java`

| Method | What it does |
|---|---|
| `findByExamination(Examination examination)` | All sick leaves linked to a specific examination. |
| `findMonthWithMostSickLeaves()` | Returns (month, year, count) rows ordered by count descending — to find which month had the most sick leaves. Custom `@Query`. |
| `findDoctorsWithMostSickLeaves()` | Returns (doctor, count) rows — which doctors issued the most sick leaves. Custom `@Query`. |
| `findByPatientId(Long patientId)` | All sick leaves for a patient (navigates through the examination to find the patient). Custom `@Query`. |

---

### `repository/UserRepository.java`

| Method | What it does |
|---|---|
| `findByUsername(String username)` | Find a user by login name. Used during login. |
| `existsByUsername(String username)` | Check if a username is already taken. |
| `findByDoctor(Doctor doctor)` | Find the user account linked to a specific doctor. |
| `findByPatient(Patient patient)` | Find the user account linked to a specific patient. |

---

## 6. Services — the business logic

Services contain the rules of the application. They are called by controllers and in turn call repositories.

---

### `service/DoctorService.java`

**`findAll()`**
Returns all doctors from the database. No rules applied.

**`findById(Long id)`**
Looks up a doctor by ID. If no doctor with that ID exists, throws a `ResourceNotFoundException` (which causes a "Not Found" error).

**`findByIdentificationNumber(String number)`**
Looks up a doctor by their ID code like "GP001". Throws error if not found.

**`findAllGps()`**
Returns only doctors where `canBeGp = true`.

**`save(Doctor doctor)`**
Saves a new doctor. Before saving, checks if an identification number was provided.
If not, it auto-generates one using `generateIdentificationNumber()`.

**`update(Long id, Doctor updated)`**
Finds the existing doctor, updates their name, specialty, and GP flag, and saves.
The identification number is never changed on update — it stays the same.

**`delete(Long id)`**
Deletes a doctor by ID. Throws error if the doctor does not exist.

**`generateIdentificationNumber(String specialty)` (private)**
Builds a short prefix from the specialty name, then counts how many doctors already have that prefix and appends the next number.
- "General Practice" → "GP" + 001, 002, 003...
- "Cardiology" → "CAR" + 001, 002...
- "Neuro Spine Surgery" → "NSS" + 001...

**`buildPrefix(String specialty)` (private)**
If the specialty is one word, takes the first 3 letters uppercased.
If multiple words, takes the first letter of each word (up to 3 words).

---

### `service/PatientService.java`

**`findAll()`**
Returns all patients.

**`findById(Long id)`**
Returns a patient by ID. Throws error if not found.

**`findByEgn(String egn)`**
Finds a patient by their national ID. Throws error if not found.

**`findByDoctor(Doctor doctor)`**
Returns all patients whose personal doctor is the given doctor.

**`countByDoctor(Doctor doctor)`**
Returns how many patients a doctor has.

**`save(Patient patient)`**
Saves a new patient. Before saving:
- Checks the EGN is not already taken. If it is, throws `DuplicateResourceException`.
- Calls `resolveDoctor()` to link the correct Doctor object.

**`update(Long id, Patient updated)`**
Finds the existing patient, applies the changes:
- If the EGN changed, checks the new EGN is not already taken by someone else.
- Updates name, EGN, personal doctor, and insurance status.

**`delete(Long id)`**
Deletes a patient. Throws error if not found.

**`resolveDoctor(Patient patient)` (private)**
When a patient is submitted, the doctor field might only have an ID number.
This method looks up the full Doctor object by that ID and sets it on the patient.

---

### `service/DiagnosisService.java`

**`findAll()`**
Returns all diagnoses.

**`findById(Long id)`**
Returns a diagnosis by ID. Throws error if not found.

**`save(Diagnosis diagnosis)`**
Saves a new diagnosis. Checks the code is not already taken. Throws `DuplicateResourceException` if so.

**`update(Long id, Diagnosis updated)`**
Finds the existing diagnosis and applies changes.
If the code changed, checks the new code is not already used elsewhere.

**`delete(Long id)`**
Deletes a diagnosis. Throws error if not found.

---

### `service/ExaminationService.java`

**`findAll()`**
Returns all examinations.

**`findById(Long id)`**
Returns one examination by ID. Throws error if not found.

**`findByPatient(Patient patient)`**
All examinations for a specific patient.

**`findByDoctor(Doctor doctor)`**
All examinations performed by a specific doctor.

**`findByDiagnosis(Diagnosis diagnosis)`**
All examinations with a specific diagnosis.

**`findPatientHistory(Patient patient)`**
All examinations for a patient, sorted newest first.

**`findByDoctorAndPeriod(Doctor doctor, LocalDate from, LocalDate to)`**
Examinations by a doctor within a date range.

**`findByPeriod(LocalDate from, LocalDate to)`**
All examinations within a date range.

**`save(Examination examination)`**
Saves a new examination with no extra rules.

**`update(Long id, Examination updated, Doctor currentDoctor)`**
Updates an existing examination.
If `currentDoctor` is not null (meaning a DOCTOR role is editing), checks that the examination belongs to that doctor. If not, throws `AccessDeniedException` — a doctor cannot edit another doctor's examination.
Only updates: date, diagnosis, treatment, and price. The doctor and patient are not changed.

**`delete(Long id)`**
Deletes an examination. Throws error if not found.

---

### `service/SickLeaveService.java`

**`findAll()`**
Returns all sick leaves.

**`findById(Long id)`**
Returns one sick leave by ID. Throws error if not found.

**`findByExamination(Examination examination)`**
All sick leaves linked to a specific examination.

**`findByPatientId(Long patientId)`**
All sick leaves for a patient.

**`save(SickLeave sickLeave)`**
Saves a new sick leave.

**`update(Long id, SickLeave updated)`**
Finds the existing sick leave and updates start date, number of days, and examination link.

**`delete(Long id)`**
Deletes a sick leave. Throws error if not found.

---

### `service/StatisticsService.java`

This service is read-only (`@Transactional(readOnly=true)`) — it never writes to the database.

**`findPatientsByDiagnosis(Diagnosis diagnosis)`**
Finds all examinations with that diagnosis, collects the unique patients from those examinations.

**`findMostFrequentDiagnosis()`**
Asks the repository for diagnoses sorted by how often they appear. Returns the top one.

**`findPatientsByGp(Doctor doctor)`**
Returns all patients whose personal doctor is the given GP.

**`totalPatientPaidAmount()`**
Total money paid by uninsured patients across all examinations. Returns 0 if null.

**`patientPaidAmountByDoctor(Doctor doctor)`**
Same, but only for a specific doctor.

**`patientCountPerGp(List<Doctor> gps)`**
Given a list of GPs, returns a map of Doctor → how many patients they have.

**`visitCountPerDoctor(List<Doctor> doctors)`**
Given a list of doctors, returns a map of Doctor → how many examinations they have performed.

**`patientVisitHistory(Patient patient)`**
All examinations for a patient, newest first.

**`examinationsByDoctorAndPeriod(Doctor doctor, LocalDate from, LocalDate to)`**
Flexible search:
- If doctor + date range given → filter by both
- If only doctor → all of their examinations
- If only date range → all examinations in that range
- If nothing given → all examinations

**`findMonthWithMostSickLeaves()`**
Finds which month and year had the most sick leaves issued. Returns a readable string like "MARCH 2026 (7 sick leaves)".

**`findDoctorsWithMostSickLeaves()`**
Returns all doctors who are tied for the most sick leaves issued.

---

### `service/UserService.java`

**`findByDoctor(Doctor doctor)`**
Returns the user account linked to a doctor, if one exists.

**`findByPatient(Patient patient)`**
Returns the user account linked to a patient, if one exists.

**`assignToDoctor(Doctor doctor, String username, String rawPassword)`**
Creates or updates a login account for a doctor:
- If a user account already exists for this doctor, updates it.
- If not, checks the username is available, then creates a new DOCTOR-role account.
The password is always stored encrypted (hashed).

**`assignToPatient(Patient patient, String username, String rawPassword)`**
Same but for a patient. Creates a PATIENT-role account.

**`updateUser(User user, String username, String rawPassword)` (private)**
Updates an existing user's username and/or password.
Only updates the password if a new one was actually provided (not blank).

**`checkUsernameAvailable(String username)` (private)**
Throws `DuplicateResourceException` if the username is already in use.

---

### `service/UserDetailsServiceImpl.java`

This service is used by Spring Security to load a user during login.

**`loadUserByUsername(String username)`**
When someone tries to log in:
1. Looks up the User by username.
2. If not found, throws `UsernameNotFoundException` (login fails).
3. If found, wraps the user's info into a `UserDetails` object that Spring Security understands — including their username, hashed password, and role (prefixed with "ROLE_" as Spring requires).

---

## 7. Controllers — the web pages

Web controllers handle requests from the browser and return HTML pages.
They follow the pattern: receive request → call service → pass data to HTML template → return page name.

`Model` is a container used to pass data to the HTML template.
`RedirectAttributes` is used to send a success/error message to the next page after a redirect.
`BindingResult` holds any validation errors after `@Valid` runs.

---

### `controller/HomeController.java`

Handles the home page and login page.

**`home()` — GET /**
Loads the home page. If the logged-in user is a PATIENT, redirects them directly to their own profile page. Otherwise shows the main dashboard.

**`login()` — GET /login**
Shows the login page. Spring Security handles the actual login logic — this just returns the HTML template.

---

### `controller/DoctorController.java`

Handles all web pages related to doctors at the `/doctors` URL.

**`list()` — GET /doctors**
Fetches all doctors from the service and passes them to the list template.

**`view()` — GET /doctors/{id}**
Loads a single doctor's detail page. Also loads the user account linked to that doctor (if any) to display on the page.

**`newForm()` — GET /doctors/new** *(ADMIN only)*
Shows an empty form for creating a new doctor.

**`create()` — POST /doctors/new** *(ADMIN only)*
Receives the submitted form data. If validation fails, shows the form again with errors. If valid, saves the doctor and redirects to the list with a success message.

**`editForm()` — GET /doctors/{id}/edit** *(ADMIN only)*
Loads a pre-filled form with the doctor's current data.

**`update()` — POST /doctors/{id}/edit** *(ADMIN only)*
Receives the submitted form, validates, saves the changes, redirects to list.

**`delete()` — POST /doctors/{id}/delete** *(ADMIN only)*
Deletes the doctor and redirects to list.

---

### `controller/PatientController.java`

Handles web pages for patients at `/patients`.

**`list()` — GET /patients** *(ADMIN or DOCTOR)*
Returns the list of all patients.

**`view()` — GET /patients/{id}**
Shows a patient's detail page including their examination history.
Special rule: if the logged-in user is a PATIENT and they try to view another patient's page, they are automatically redirected to their own page.

**`myProfile()` — GET /patients/my** *(PATIENT only)*
Shortcut for a patient to view their own profile.

**`newForm()` — GET /patients/new** *(ADMIN only)*
Shows form to create a new patient. Also loads all GPs so the admin can pick one.

**`create()` — POST /patients/new** *(ADMIN only)*
Saves the new patient after validation. Checks that a GP was selected.

**`editForm()` — GET /patients/{id}/edit** *(ADMIN only)*
Shows a pre-filled edit form with the patient's current data and the list of GPs.

**`update()` — POST /patients/{id}/edit** *(ADMIN only)*
Saves the updated patient after validation.

**`delete()` — POST /patients/{id}/delete** *(ADMIN only)*
Deletes the patient.

---

### `controller/DiagnosisController.java`

Handles web pages for diagnoses at `/diagnoses`.

**`list()` — GET /diagnoses**
Returns all diagnoses.

**`newForm()` — GET /diagnoses/new** *(ADMIN only)*
Shows the creation form.

**`create()` — POST /diagnoses/new** *(ADMIN only)*
Saves the new diagnosis after validation.

**`editForm()` — GET /diagnoses/{id}/edit** *(ADMIN only)*
Shows the edit form.

**`update()` — POST /diagnoses/{id}/edit** *(ADMIN only)*
Saves the updated diagnosis.

**`delete()` — POST /diagnoses/{id}/delete** *(ADMIN only)*
Deletes the diagnosis.

---

### `controller/ExaminationController.java`

Handles web pages for examinations at `/examinations`.

**`list()` — GET /examinations**
Returns examinations filtered by the logged-in user's role:
- PATIENT → only their own examinations
- DOCTOR → only their own examinations
- ADMIN → all examinations

**`newForm()` — GET /examinations/new** *(ADMIN or DOCTOR)*
Shows the form to create a new examination.
For DOCTORS, the doctor dropdown only shows themselves.
For ADMIN, it shows all doctors.

**`create()` — POST /examinations/new** *(ADMIN or DOCTOR)*
Saves the new examination.
If the logged-in user is a DOCTOR, their doctor identity is automatically set — they cannot create an examination for another doctor.

**`editForm()` — GET /examinations/{id}/edit** *(ADMIN or DOCTOR)*
Shows the edit form.
If a DOCTOR tries to edit an examination that belongs to another doctor, they are redirected with a "forbidden" error.

**`update()` — POST /examinations/{id}/edit** *(ADMIN or DOCTOR)*
Saves the updated examination. Passes the current doctor to the service, which enforces that doctors can only edit their own records.

**`delete()` — POST /examinations/{id}/delete** *(ADMIN or DOCTOR)*
Deletes the examination.
Doctors can only delete their own examinations — if they try to delete another's, they see an error message.

---

### `controller/SickLeaveController.java`

Handles web pages for sick leaves at `/sickleaves`.

**`list()` — GET /sickleaves**
Shows sick leaves filtered by role:
- PATIENT → only their own sick leaves
- ADMIN or DOCTOR → all sick leaves

**`newForm()` — GET /sickleaves/new** *(ADMIN or DOCTOR)*
Shows the creation form. For DOCTORS, only shows examinations they performed. For ADMIN, shows all.

**`create()` — POST /sickleaves/new** *(ADMIN or DOCTOR)*
Saves the new sick leave.
Extra check for DOCTORS: verifies the chosen examination belongs to them before saving.

**`editForm()` — GET /sickleaves/{id}/edit** *(ADMIN or DOCTOR)*
Shows the edit form.
DOCTORS cannot edit sick leaves from other doctors' examinations.

**`update()` — POST /sickleaves/{id}/edit** *(ADMIN or DOCTOR)*
Saves changes.
DOCTORS cannot reassign the sick leave to a different examination — their examination link stays locked.

**`delete()` — POST /sickleaves/{id}/delete** *(ADMIN or DOCTOR)*
Deletes the sick leave. DOCTORS can only delete their own.

---

### `controller/StatisticsController.java`

Handles the statistics web pages at `/statistics`. ADMIN and DOCTOR only.

**`index()` — GET /statistics**
The main statistics dashboard. Loads:
- Most frequently used diagnosis
- Total amount paid by uninsured patients
- How many patients each GP has
- How many examinations each doctor has performed
- Which month had the most sick leaves
- Which doctors issued the most sick leaves

**`byDiagnosis()` — GET /statistics/by-diagnosis**
Page that lets you pick a diagnosis and see which patients have been diagnosed with it.

**`byGp()` — GET /statistics/by-gp**
Page that lets you pick a GP and see their patients.

**`byDoctorAndPeriod()` — GET /statistics/by-doctor-period**
Page to filter examinations by doctor and/or date range.

**`paidByDoctor()` — GET /statistics/paid-by-doctor**
Shows how much money each doctor's uninsured patients have paid.

---

### `controller/UserAccountController.java`

Handles creating/updating login accounts for doctors and patients. ADMIN only.

**`doctorAssignForm()` — GET /doctors/{id}/assign-user**
Shows the form to create or update the login account for a doctor.
If a user account already exists, pre-fills the username.

**`doctorAssign()` — POST /doctors/{id}/assign-user**
Saves the user account:
- If it is a brand new account, a password is required.
- If it already exists, password is optional (leave blank to keep the current one).
- If the username is already taken, shows an error.

**`patientAssignForm()` — GET /patients/{id}/assign-user**
Same as above but for a patient.

**`patientAssign()` — POST /patients/{id}/assign-user**
Same logic as `doctorAssign()` but for a patient.

---

## 8. API Controllers — the REST API

These controllers work the same way as web controllers but return **JSON data** instead of HTML pages.
They live under the `/api/v1/` URL prefix.
Authentication uses **HTTP Basic Auth** — you include a username and password with every request.
They are stateless — no sessions or cookies.

HTTP methods used:
- `GET` — retrieve data
- `POST` — create new data
- `PUT` — update existing data
- `DELETE` — remove data

HTTP status codes returned:
- `200 OK` — success
- `201 Created` — new record created
- `204 No Content` — deleted successfully
- `400 Bad Request` — validation failed
- `401 Unauthorized` — not logged in
- `403 Forbidden` — logged in but not allowed
- `404 Not Found` — record does not exist
- `409 Conflict` — duplicate (e.g. EGN already exists)
- `500 Internal Server Error` — unexpected error

---

### `api/controller/DoctorApiController.java`

Base URL: `/api/v1/doctors`

**`list()` — GET /api/v1/doctors**
Returns a JSON array of all doctors. Any authenticated user.

**`getById()` — GET /api/v1/doctors/{id}**
Returns one doctor as JSON. Any authenticated user.

**`create()` — POST /api/v1/doctors** *(ADMIN only)*
Reads the doctor data from the JSON request body, creates the doctor, returns the created doctor as JSON with status 201.

**`update()` — PUT /api/v1/doctors/{id}** *(ADMIN only)*
Updates a doctor's name, specialty, and GP flag. Returns the updated doctor as JSON.

**`delete()` — DELETE /api/v1/doctors/{id}** *(ADMIN only)*
Deletes the doctor. Returns 204 with no body.

---

### `api/controller/PatientApiController.java`

Base URL: `/api/v1/patients`

**`list()` — GET /api/v1/patients** *(ADMIN or DOCTOR)*
Returns all patients as a JSON array.

**`getById()` — GET /api/v1/patients/{id}**
Returns one patient as JSON.

**`create()` — POST /api/v1/patients** *(ADMIN only)*
Looks up the doctor by the provided `personalDoctorId`, creates the patient, returns 201.

**`update()` — PUT /api/v1/patients/{id}** *(ADMIN only)*
Updates the patient. Returns updated patient.

**`delete()` — DELETE /api/v1/patients/{id}** *(ADMIN only)*
Deletes the patient. Returns 204.

---

### `api/controller/DiagnosisApiController.java`

Base URL: `/api/v1/diagnoses`

**`list()` — GET /api/v1/diagnoses**
All diagnoses as JSON.

**`getById()` — GET /api/v1/diagnoses/{id}**
One diagnosis as JSON.

**`create()` — POST /api/v1/diagnoses** *(ADMIN only)*
Creates a new diagnosis. Returns 201.

**`update()` — PUT /api/v1/diagnoses/{id}** *(ADMIN only)*
Updates code and description.

**`delete()` — DELETE /api/v1/diagnoses/{id}** *(ADMIN only)*
Deletes the diagnosis. Returns 204.

---

### `api/controller/ExaminationApiController.java`

Base URL: `/api/v1/examinations`

**`list()` — GET /api/v1/examinations**
Returns examinations filtered by the caller's role — same rules as the web UI:
- PATIENT → own examinations
- DOCTOR → own examinations
- ADMIN → all

**`getById()` — GET /api/v1/examinations/{id}**
Returns one examination.

**`create()` — POST /api/v1/examinations** *(ADMIN or DOCTOR)*
Creates a new examination. Looks up the doctor, patient, and diagnosis by their IDs.
If the caller is a DOCTOR, the doctor is always set to themselves — they cannot specify another doctor.
Price defaults to 0 if not provided.

**`update()` — PUT /api/v1/examinations/{id}** *(ADMIN or DOCTOR)*
Updates date, diagnosis, treatment, and price.
Doctors can only update their own examinations (enforced by the service).

**`delete()` — DELETE /api/v1/examinations/{id}** *(ADMIN or DOCTOR)*
Deletes the examination. Doctors can only delete their own.

---

### `api/controller/SickLeaveApiController.java`

Base URL: `/api/v1/sickleaves`

**`list()` — GET /api/v1/sickleaves**
All sick leaves as JSON.

**`getById()` — GET /api/v1/sickleaves/{id}**
One sick leave.

**`listByPatient()` — GET /api/v1/sickleaves/patient/{patientId}**
All sick leaves for a specific patient.

**`create()` — POST /api/v1/sickleaves** *(ADMIN or DOCTOR)*
Creates a sick leave. Looks up the examination by ID.

**`update()` — PUT /api/v1/sickleaves/{id}** *(ADMIN or DOCTOR)*
Updates start date, number of days, and examination link.

**`delete()` — DELETE /api/v1/sickleaves/{id}** *(ADMIN or DOCTOR)*
Deletes the sick leave. Returns 204.

---

### `api/controller/StatisticsApiController.java`

Base URL: `/api/v1/statistics` — ADMIN and DOCTOR only.

| Endpoint | What it returns |
|---|---|
| `GET /most-frequent-diagnosis` | The diagnosis that appears in the most examinations |
| `GET /total-patient-paid` | Total money paid by uninsured patients (a single number) |
| `GET /paid-by-doctor` | A map of doctorId → total paid by that doctor's uninsured patients |
| `GET /patients-by-diagnosis?diagnosisId=X` | All patients who have been given diagnosis X |
| `GET /patients-by-gp?doctorId=X` | All patients whose personal GP is doctor X |
| `GET /examinations-by-doctor-period?doctorId=X&from=Y&to=Z` | Filtered examinations (all params optional) |
| `GET /patient-count-per-gp` | Map of doctorId → number of patients |
| `GET /visit-count-per-doctor` | Map of doctorId → number of examinations performed |
| `GET /month-most-sick-leaves` | A string like "MARCH 2026 (7 sick leaves)" |
| `GET /doctors-most-sick-leaves` | List of doctors tied for the most sick leaves issued |

---

## 9. Config — setup and security

---

### `config/SecurityConfig.java`

This class tells Spring Security how to protect the application.
It defines **two separate security filter chains** — one for the API, one for the web UI.

**`passwordEncoder()` — @Bean**
Creates a BCrypt password encoder. BCrypt is a strong hashing algorithm — it converts a plain-text password into a scrambled string that cannot be reversed. This is what gets stored in the database.

**`authenticationProvider()` — @Bean**
Sets up how login works: use the `UserDetailsServiceImpl` to look up users, and the BCrypt encoder to check passwords.

**`apiFilterChain()` — @Bean @Order(1)**
Security rules for `/api/**` URLs (order 1 = checked first):
- Requires authentication for every request
- Uses HTTP Basic Auth — credentials are sent with every request as a header
- Returns `401 Unauthorized` for unauthenticated requests (not a login page redirect)
- Returns `403 Forbidden` for authenticated users without the required role
- Sessions are disabled (stateless) — no cookies
- CSRF protection is disabled (not needed for APIs that don't use browser cookies)

**`filterChain()` — @Bean @Order(2)**
Security rules for the web UI (order 2 = checked second, i.e. everything not matched by the API chain):
- `/h2-console/**`, `/css/**`, `/js/**` are open to all
- ADMIN-only pages: `/admin/**`, create/edit/delete for doctors, patients, diagnoses
- ADMIN or DOCTOR: create/edit/delete for examinations and sick leaves
- Everything else requires being logged in
- Login is via a form page at `/login`
- After logout, redirects to `/login?logout`
- CSRF protection is enabled for the web UI (protects form submissions)

---

### `config/CurrentUserHelper.java`

A helper component that any controller can use to find out who is currently logged in.

**`getCurrentUser()`**
Reads the currently authenticated user from Spring Security's context (like a session store).
Returns null if nobody is logged in.
Otherwise, looks up the User record in the database by username and returns it.

---

### `config/DataInitializer.java`

This runs automatically once when the application starts (`CommandLineRunner`).
It checks if there are already users in the database. If yes, it does nothing (avoids duplicating data on restart).
If the database is empty, it creates a set of sample data:

**Doctors created:**
- Dr. Ivan Petrov — General Practice (GP) → ID: GP001
- Dr. Maria Georgieva — General Practice (GP) → ID: GP002
- Dr. Todor Dimitrov — Cardiology → ID: CAR001
- Dr. Elena Ivanova — Neurology → ID: NEU001

**Patients created:**
- Georgi Stoev (EGN: 9001011234) — GP: Petrov, insured
- Ana Kostadinova (EGN: 9205152345) — GP: Petrov, not insured
- Petar Nikolov (EGN: 8811203456) — GP: Georgieva, insured
- Iva Todorova (EGN: 9507074567) — GP: Georgieva, insured

**Diagnoses created:** J06, I10, G43, J18

**Examinations and sick leaves:** Several sample visits with associated sick leaves are created.

**User accounts created:**
| Username | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN |
| dr.petrov | doctor123 | DOCTOR (linked to Dr. Ivan Petrov) |
| dr.georgieva | doctor123 | DOCTOR (linked to Dr. Maria Georgieva) |
| g.stoev | patient123 | PATIENT (linked to Georgi Stoev) |
| a.kostadinova | patient123 | PATIENT (linked to Ana Kostadinova) |

---

## 10. Exceptions — error handling

---

### `exception/ResourceNotFoundException.java`

Thrown when something is looked up by ID but does not exist.
`@ResponseStatus(HttpStatus.NOT_FOUND)` means Spring will return HTTP 404 when this is thrown.

Two ways to create it:
- `new ResourceNotFoundException("Some message")` — custom message
- `new ResourceNotFoundException("Doctor", 99L)` → produces "Doctor with id 99 not found"

---

### `exception/DuplicateResourceException.java`

Thrown when trying to create something that already exists (e.g. duplicate EGN or diagnosis code).
`@ResponseStatus(HttpStatus.CONFLICT)` → HTTP 409.

---

### `exception/AccessDeniedException.java`

Thrown when a user tries to do something they are not allowed (e.g. a doctor editing another doctor's examination).
`@ResponseStatus(HttpStatus.FORBIDDEN)` → HTTP 403.

---

### `exception/GlobalExceptionHandler.java`

`@ControllerAdvice(basePackages = "com.medicalrecords.controller")`
This applies only to the web page controllers. When an exception occurs, instead of crashing, this catches it and shows a user-friendly HTML error page.

**`handleNotFound()`** — catches `ResourceNotFoundException` → shows error page with "Not Found" title
**`handleDuplicate()`** — catches `DuplicateResourceException` → shows "Duplicate Entry" error page
**`handleAccessDenied()`** — catches `AccessDeniedException` → shows "Access Denied" error page
**`handleGeneral()`** — catches any other unexpected exception → shows generic "Error" page

---

### `exception/ApiExceptionHandler.java`

`@RestControllerAdvice(basePackages = "com.medicalrecords.api")`
This applies only to the API controllers. Returns JSON error responses instead of HTML pages.
Uses the standard `ProblemDetail` format (RFC 9457) which looks like:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Patient with id 99 not found",
  "instance": "/api/v1/patients/99"
}
```

**`handleNotFound()`** — 404 JSON response
**`handleDuplicate()`** — 409 Conflict JSON response
**`handleAccessDenied()`** — 403 Forbidden JSON response (for our custom exception)
**`handleSpringAccessDenied()`** — 403 Forbidden JSON response (for Spring Security's access denied)
**`handleValidation()`** — 400 Bad Request with a map of field → error message, e.g.:
```json
{
  "status": 400,
  "errors": { "egn": "EGN must be exactly 10 digits" }
}
```
**`handleGeneral()`** — 500 Internal Server Error for unexpected exceptions

---

## 11. Tests

Tests verify that the code works correctly without having to manually click through the app.
All tests use the `test` profile which means they use an **H2 in-memory database** — a temporary database that exists only during the test run and is wiped clean after.

---

### `MedicalRecordsApplicationTest.java`

One test: `contextLoads()` — just starts the whole application and checks it doesn't crash.
This is a sanity check that all the wiring is correct.

---

### `service/DoctorServiceTest.java`

Uses Mockito — the `DoctorRepository` is a **fake** (mock) that returns whatever we tell it to.
This lets us test the service's logic without needing a real database.

| Test | What it checks |
|---|---|
| `findAll_returnsAllDoctors` | Service returns what the repository gives it |
| `findById_existingId_returnsDoctor` | Correct doctor is returned by ID |
| `findById_missingId_throwsResourceNotFoundException` | Throws the right error for missing IDs |
| `save_newDoctor_generatesIdentificationNumber` | Auto-generates "CAR001" for Cardiology |
| `save_multiWordSpecialty_buildsInitialsPrefix` | "General Practice" with 2 existing → "GP003" |
| `save_doctorWithExistingIdNumber_doesNotOverwrite` | Pre-set ID number is kept as-is |
| `update_preservesIdentificationNumber` | After update, the ID number is unchanged |
| `delete_existingDoctor_deletesSuccessfully` | Delete is called on the repository |
| `delete_missingDoctor_throwsResourceNotFoundException` | Throws error when deleting non-existent doctor |
| `findAllGps_returnsOnlyGps` | All returned doctors have canBeGp = true |

---

### `service/PatientServiceTest.java`

| Test | What it checks |
|---|---|
| `findAll_returnsAllPatients` | Service returns the full list |
| `findById_found_returnsPatient` | Correct patient returned |
| `findById_notFound_throwsException` | Error on missing patient |
| `save_duplicateEgn_throwsDuplicateException` | Cannot save two patients with the same EGN |
| `countByDoctor_returnCount` | Delegates count correctly to repository |

---

### `service/ExaminationServiceTest.java`

| Test | What it checks |
|---|---|
| `findById_existing_returnsExamination` | Correct examination returned |
| `findById_missing_throwsException` | Error for missing examination |
| `update_ownExamination_succeeds` | Doctor can update their own examination |
| `update_anotherDoctorsExamination_throwsAccessDenied` | Doctor cannot edit another's examination |
| `findByPatient_returnsList` | Correct list returned for a patient |
| `save_persistsExamination` | Save is delegated to the repository |

---

### `repository/DoctorRepositoryIntegrationTest.java`

Uses `@DataJpaTest` — a real H2 in-memory database with Liquibase migrations applied.
Tests the actual SQL queries and database constraints.

| Test | What it checks |
|---|---|
| `findByCanBeGpTrue_returnsOnlyGpDoctors` | Only GP doctors come back |
| `findByIdentificationNumber_existingNumber_returnsDoctor` | Correct doctor found by ID code |
| `findByIdentificationNumber_unknownNumber_returnsEmpty` | Unknown code returns empty |
| `existsByIdentificationNumber_existing_returnsTrue` | Exists check works |
| `existsByIdentificationNumber_missing_returnsFalse` | Non-existent returns false |
| `save_duplicateIdentificationNumber_throwsDataIntegrityViolation` | Database rejects duplicate ID codes |
| `findAll_returnsAllSavedDoctors` | Returns all 2 doctors from setup |
| `delete_removesDoctor` | Doctor is gone after delete |

---

### `repository/PatientRepositoryIntegrationTest.java`

| Test | What it checks |
|---|---|
| `findByPersonalDoctor_returnsOnlyPatientsOfThatDoctor` | Only the right doctor's patients come back |
| `findByPersonalDoctor_doctorWithNoPatients_returnsEmpty` | New doctor has no patients |
| `countByPersonalDoctor_returnsCorrectCount` | Correct count for each doctor |
| `findByEgn_existingEgn_returnsPatient` | Patient found by EGN |
| `findByEgn_unknownEgn_returnsEmpty` | Unknown EGN returns empty |
| `existsByEgn_existing_returnsTrue` | EGN exists check works |
| `existsByEgn_missing_returnsFalse` | Non-existent EGN returns false |
| `save_duplicateEgn_throwsDataIntegrityViolation` | Database rejects duplicate EGN |
| `healthInsuranceStatus_persistedCorrectly` | Insurance flag saved and retrieved correctly |

---

### `repository/ExaminationRepositoryIntegrationTest.java`

| Test | What it checks |
|---|---|
| `findByPatient_returnsOnlyThatPatientsExaminations` | Filtering by patient works |
| `findByDoctor_returnsOnlyThatDoctorsExaminations` | Filtering by doctor works |
| `findByDiagnosis_returnsAllExaminationsWithThatDiagnosis` | Filtering by diagnosis works (flu appears 3 times) |
| `findPatientHistory_returnsInDescendingDateOrder` | Newest examination comes first |
| `sumPatientPaidExaminations_onlyCountsUninsuredPatients` | Only uninsured totals count (25 + 15 = 40) |
| `sumPatientPaidExaminations_allInsured_returnsNull` | When everyone is insured, sum is null |
| `sumPatientPaidByDoctor_onlyCountsThatDoctorsUninsuredExaminations` | Per-doctor uninsured total is correct |
| `countByDoctor_returnsCorrectVisitCount` | Correct visit counts per doctor |
| `findDiagnosisFrequency_mostFrequentFirst` | Flu (3 times) comes before hypertension (1 time) |
| `findByDateBetween_returnsOnlyExaminationsInRange` | Date range filter works correctly |
| `findByDoctorAndDateBetween_filtersOnBothCriteria` | Combined filter by doctor + date works |
| `paidByNhif_setBasedOnPatientInsuranceStatus` | @PrePersist correctly sets paidByNhif |

---

## 12. Main entry point

### `MedicalRecordsApplication.java`

```java
@SpringBootApplication
public class MedicalRecordsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicalRecordsApplication.class, args);
    }
}
```

`@SpringBootApplication` is a shortcut for three annotations at once:
- "Scan this package for all classes to manage"
- "Enable auto-configuration (set up Spring automatically)"
- "This is a configuration class"

`main()` is the very first method that runs when you start the application.
`SpringApplication.run(...)` boots up the entire Spring framework, connects to the database, runs Liquibase migrations, starts the web server on port 8080, and then runs `DataInitializer` to seed sample data.
