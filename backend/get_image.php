<?php

include_once "../config/database.php";

$conn = OpenCon();

$point_id = 2;//$_POST['point_id'];

GetImage($conn, $point_id);

function GetImage($conn, $point_id)
{
    $query  = "
    SELECT image AS point_image
    FROM point
    WHERE id = '$point_id'
    ";

    try
    {
        $result = $conn->query($query); 

        if ($result -> num_rows > 0)
        {				
            $data = mysqli_fetch_all($result, MYSQLI_ASSOC);

            header('Content-Type: image/png');

            $pathToImage = $data['point_image'];
            
            print("test");
            
            echo "<img src='$pathToImage'></img>";			
        }
    }
    catch(Exception $e)
    {
        echo json_encode(array("message" => $e -> getMessage()), JSON_UNESCAPED_UNICODE);
    }

    CloseCon($conn);
}

?>