import sys
from numpy  import *
import yaml

if len(sys.argv) != 2:
  print("Usage : ./randomWalk.py [yamlFile]")
  print(sys.argv)
  exit(1)

yamlFile = sys.argv[1]

def readGraphFile(yamlFile):
  f = open(yamlFile)
  dataMap = yaml.safe_load(f)
  f.close()
  del dataMap['Corners']
  numNodes = size(dataMap.keys())
  m = matrix(zeros((numNodes, numNodes)))
  for start, edges in dataMap.iteritems():
    for end in edges:
      m[start, end] = 1
  return m

def computeTransitionProbabilities(paths):
  numNodes = paths.shape[0]
  print(numNodes)
  degrees = sum(paths, axis=0)[0][0]
  print(degrees)
  for i in range(numNodes):
    for j in range(numNodes):
      paths[i, j] = paths[i, j]/degrees[0, i]
  return paths

print(computeTransitionProbabilities(readGraphFile(yamlFile)))

