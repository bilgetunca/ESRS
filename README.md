-------Android-Based Campus Emergency Reporting Application

ESRS (Emergency Situation Reporting System) is an Android-based mobile application designed to streamline emergency reporting within a university campus. The system enables users to submit incident reports, share real-time locations, receive notifications, and allows administrators to manage emergencies through a Firestore-backed infrastructure.
------Features
	•	Real-time Emergency Reporting
Users can quickly submit emergency incidents with descriptions and optional media attachments.
	•	Live Location Sharing
Automatic GPS-based location sharing ensures responders receive accurate real-time coordinates.
	•	Role-Based Notification System
Department administrators receive incident notifications through Firebase Cloud Messaging (FCM).
	•	Google Maps Integration
Incidents are visualized on the map interface with geolocation markers.
	•	Admin Dashboard (Mobile Version)
Admin users can view, filter, and manage emergency reports.
	•	Firestore-Based Backend
Secure and scalable backend using Firebase Authentication, Firestore Database, Cloud Storage, and FCM.

------Tech Stack
Languages & Frameworks
	•	Java
	•	XML
	•	Android SDK

Backend & Cloud Services
	•	Firebase Authentication
	•	Firebase Firestore
	•	Firebase Cloud Storage
	•	Firebase Cloud Messaging (FCM)
	•	Cloud Functions

APIs
	•	Google Maps SDK
	•	Play Services Location API

  -------Core Modules
User Module
	•	User registration and authentication
	•	Real-time emergency report submission
	•	Location permissions and GPS tracking
	•	Incident browsing & filtering

Admin Module
	•	Receive high-priority push notifications
	•	Map-based incident management
	•	Update incident statuses
	•	Role-based access control
  
-------Project Structure
app/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/esrs/...         # Activities, ViewModels, adapters
 │   │   ├── res/layout                # XML UI screens
 │   │   ├── res/values                # colors.xml, strings.xml, styles.xml
 │   │   ├── AndroidManifest.xml
 ├── build.gradle (Module)
build.gradle (Project)
settings.gradle
gradle/

-------Getting Started

Run the application in Android Studio:git clone https://github.com/bilgetunca/ESRS.git
	1.	Clone the repository
  2.	Open the project in Android Studio
	3.	Sync Gradle
	4.	Add your own Firebase google-services.json
	5.	Run on Android device (API 21+)

  -------Security & Data Handling
  	•	Firebase rules restrict unauthorized read/write access
	•	Sensitive user information protected using Firebase Authentication
	•	Location data transmitted securely over HTTPS
	•	Logs and reports stored with timestamps for auditability

  -------- Future Improvements
	•	Voice-to-text incident reporting
	•	Offline incident caching
	•	Advanced analytics for admin dashboard
	•	Bluetooth beacon indoor positioning
