import sys
import re

if sys.argv[2] == "common":
	headerF = open("""/home/eric/CodeJam/lib/common.h""", 'r')
elif sys.argv[2] == "graph":
	headerF = open("""/home/eric/CodeJam/lib/graph.h""", 'r')
else:
	headerF = open("""/home/eric/CodeJam/lib/geom.h""", 'r')
sourceFile = open( sys.argv[1], 'r')
backupFile = open( sys.argv[1] + 'backup', 'w')

headerContents = headerF.read()
sourceContents = sourceFile.read()

#print(headerContents)

backupFile.write(sourceContents)

replaceGeom = re.compile(r"""
(//STARTCOMMON)               # Start of a numeric entity reference
(.*)
(//STOPCOMMON)
""", re.VERBOSE | re.DOTALL | re.MULTILINE )


#make sure backslashes stay that way
headerContents = headerContents.replace("\\", "\\\\")
replacement = '\\1\n' + headerContents + '\n\\3'
#print(replacement)
#print (sourceContents)
sourceContents = replaceGeom.sub( replacement,  sourceContents )

#print( replaceGeom.search(sourceContents).groups() )

sourceFile = open( sys.argv[1], 'w')
sourceFile.write( sourceContents )

#print(headerContents)
#print(sourceContents)
