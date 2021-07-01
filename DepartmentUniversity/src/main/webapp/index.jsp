<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
	crossorigin="anonymous">
	<link rel="stylesheet" href="/static/css/font-awesome.min.css" />
	<link rel="stylesheet" th:href="@{/static/table.css}" />
    <script src="https://use.fontawesome.com/fec1def459.js"></script>
<title>university!</title>
</head>
<body>
	<div class="container">
          <div class="navigation">          
            <ul class="nav justify-content-end" style="background-color: #f5f5f5;">         
                <li class="nav-item" ><a class="nav-link" style="color: #333;" href="/DepartmentUniversity/students"><i class="fa fa-users"></i>Students</a></li>
                <li class="nav-item"><a class="nav-link" style="color: #333;" href="/DepartmentUniversity/teachers"><i class="fa fa-users"></i>Teachers</a></li>
                <li class="nav-item"><a class="nav-link" style="color: #333;" href="/DepartmentUniversity/groups"><i class="fa fa-graduation-cap"></i>Groups</a></li>
                <li class="nav-item"><a class="nav-link" style="color: #333;" href="/DepartmentUniversity/courses"><i class="fa fa-book"></i>Courses</a></li>
                <li class="nav-item"><a class="nav-link" style="color: #333;" href="/DepartmentUniversity/classrooms"><i class="fa fa-university"></i>Classrooms</a></li>
                <li class="nav-item"><a class="nav-link" style="color: #333;" href="/DepartmentUniversity/timetables"><i class="fa fa-calendar"></i>Timetables</a></li>
                <li class="nav-item"><a class="btn btn-sm btn-dark" href="/DepartmentUniversity/" >Login</a></li>           
            </ul>
          </div>                
   </div>
</body>
</html>