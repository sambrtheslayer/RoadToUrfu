<?php

include_once "../config/database.php";

$point_id = $_POST["point_id"];

$conn = OpenCon();

GetPointOne($conn, $point_id);

// Get data point by id; need to use that function at the same time with GetAltDataForPointOne() in get_alt_language_point_one.php
function GetPointOne($conn, $point_id)
{    
    $query  = "
    SELECT * FROM point
    WHERE id = '$point_id'
    ";
    
    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_ASSOC);

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