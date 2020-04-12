for i in $(seq $1)
do
    echo '{"name":"Foobar", "message":"Hello nerd"}' | fn invoke $2 $3
done
