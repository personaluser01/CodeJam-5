python c:\codejam\codejam\lib\replaceInFile.py main.cpp graph
make clean all  > out.txt 2>&1
rem cat input.txt | project
python gen_data.py > data.txt
cat data.txt | project > out.txt
cat data.txt | p2 > out2.txt

diff out.txt out2.txt
