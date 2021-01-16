<?php

include_once "../config/database.php";

$conn = OpenCon();

$id = $_POST['id'];

GetPhotoesById($conn, $id);

function GetPhotoesById($conn, $id)
{
    $query  = "
    SELECT point_photoes.photo AS photoes
    FROM point_photoes
    INNER JOIN point
    ON point_photoes.point_id = point.id
    WHERE point.id = '$id'
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