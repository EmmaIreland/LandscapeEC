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
  degrees = sum(paths, axis=0)[0][0]
  for i in range(numNodes):
    for j in range(numNodes):
      paths[i, j] = paths[i, j]/degrees[0, i]
  return paths

def removeRowAndColumn(paths, index):
  r = delete(paths,index, 0)
  r = delete(r, index, 1)
  return r

def computeExpectedWalkTimeToNode(paths, node):
  sub_matrix = removeRowAndColumn(paths, node)
  print(node)
  d = (identity(sub_matrix.shape[0], float) - sub_matrix).I
  row_sums = d.sum(axis=1)
  r = list(array(row_sums)[:, 0])
  return r[0:node] + [0] + r[node:]

paths = computeTransitionProbabilities(readGraphFile(yamlFile))

distance_matrix = matrix([ computeExpectedWalkTimeToNode(paths, i) 
			for i in range(0, paths.shape[0]) ])

eccentricities = distance_matrix.max(axis=0)

print(eccentricities)
print(eccentricities.max())
print(eccentricities.min())
