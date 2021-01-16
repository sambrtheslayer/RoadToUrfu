<?php

include_once "../config/database.php";

$given_category = $_POST['category'];

$conn = OpenCon();

GetPointsNotMap($conn, $given_category);

// Getting all points
function GetPointsNotMap($conn, $given_category)
{    
    $query  = "
    SELECT point.id AS point_id, point.name AS point_name, data_chinese.name AS point_alt_name
    FROM point
    INNER JOIN data_chinese
    ON point.id = data_chinese.id
    WHERE point.category = '$given_category'
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