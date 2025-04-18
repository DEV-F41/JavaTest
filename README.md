# JavaTest
JAVA Backend detail User Registration (Signup) , User Login , Get User Profile (Protected Route)
User Registration:

รับข้อมูล email, password, name
เข้ารหัส password ด้วย BCrypt
ตรวจสอบ email ซ้ำในระบบ
เก็บข้อมูลในฐานข้อมูล SQLite
ส่ง response: {"message": "User registered successfully"}
User Login:

รับ email และ password
ตรวจสอบข้อมูลกับฐานข้อมูล
สร้าง JWT Token เมื่อข้อมูลถูกต้อง
ส่ง response: {"accessToken": "<JWT_TOKEN>"}
Get User Profile:

Protected route ที่ต้องใช้ JWT Token
ดึงข้อมูล user จาก token
ส่ง response: {"id": 6, "name": "Test User", "email": "test4@example.com"}
