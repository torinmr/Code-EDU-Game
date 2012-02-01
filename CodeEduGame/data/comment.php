<html><body>
<?php
$comment = $_POST['comment'];
$name = $_POST['name'];
$email = $_POST['email'];

echo "Thank you for commenting!";
$headers = 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
$emailbody = "<p>You have recieved a new message from the comment form on codeedugame.appspot.com.</p> 
                  <p><strong>Name: </strong> {$name} </p> 
                  <p><strong>Email Address: </strong> {$email} </p> 
                  <p><strong>Comment: {$comment} </p>";
				  
mail("javajackdev@gmail.com", "Comment from {$email}", $emailbody, $headers);

?>
</body></html>