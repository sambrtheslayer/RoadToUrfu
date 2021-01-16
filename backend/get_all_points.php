<?php

/*
!
Пересмотреть файл
!
*/

include_once "../config/database.php";

$conn = OpenCon();

GetAllPoints($conn);    

// Getting all points
function GetAllPoints($conn)
{
    $query  = "
    SELECT point.id AS point_id,
            point.name AS point_name,
            data_chinese.name AS point_alt_name,
            point.latitude AS point_latitude,
            point.longitude AS point_longitude,
            point.classroom AS point_classroom,
            point.description AS point_description,
            data_chinese.description AS point_alt_description
    FROM point
    INNER JOIN data_chinese
    ON point.id = data_chinese.id
    ";
    
    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_ASSOC);

            /*
            point_id
            point_name
            point_alt_name
            point_latitude
            point_longitude
            point_image
            point_description
            point_alt_description
            */

            header('Content-Type: application/json');
            
            echo json_encode($data);				
        }
    }
    catch(Exception $e)
    {
        echo json_encode(array("message" => $e -> getMessage()), JSON_UNESCAPED_UNICODE);
    }

    CloseCon($conn);
}

?>