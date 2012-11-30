Scenario: Generate a neighboorhood

As a machine learning researcher
I want to be able to generate a neighborhood of a location
in order to find potential mates for an individual

Given a non-toroidal world of size [10]
When I compute the neighborhood of [5] with radius 1
Then the result contains:
[4]
[5]
[6]

Given a non-toroidal world of size [10]
When I compute the neighborhood of [5] with radius 3
Then the result contains:
[2]
[3]
[4]
[5]
[6]
[7]
[8]

Given a non-toroidal world of size [10]
When I compute the neighborhood of [8] with radius 3
Then the result contains:
[5]
[6]
[7]
[8]
[9]

Given a toroidal world of size [10]
When I compute the neighborhood of [8] with radius 3
Then the result contains:
[0]
[1]
[5]
[6]
[7]
[8]
[9]

Given a non-toroidal world of size [10, 10]
When I compute the neighborhood of [5, 5] with radius 1
Then the result contains:
[4, 4]
[4, 5]
[4, 6]
[5, 4]
[5, 5]
[5, 6]
[6, 4]
[6, 5]
[6, 6]

Given a non-toroidal world of size [10, 10]
When I compute the neighborhood of [5, 5] with radius 2
Then the result contains:
[3, 3]
[3, 4]
[3, 5]
[3, 6]
[3, 7]
[4, 3]
[4, 4]
[4, 5]
[4, 6]
[4, 7]
[5, 3]
[5, 4]
[5, 5]
[5, 6]
[5, 7]
[6, 3]
[6, 4]
[6, 5]
[6, 6]
[6, 7]
[7, 3]
[7, 4]
[7, 5]
[7, 6]
[7, 7]

Given a non-toroidal world of size [10, 10]
When I compute the neighborhood of [9, 1] with radius 2
Then the result contains:
[7, 0]
[7, 1]
[7, 2]
[7, 3]
[8, 0]
[8, 1]
[8, 2]
[8, 3]
[9, 0]
[9, 1]
[9, 2]
[9, 3]

Given a toroidal world of size [10, 10]
When I compute the neighborhood of [9, 1] with radius 2
Then the result contains:
[0, 0]
[0, 1]
[0, 2]
[0, 3]
[0, 9]
[1, 0]
[1, 1]
[1, 2]
[1, 3]
[1, 9]
[7, 0]
[7, 1]
[7, 2]
[7, 3]
[7, 9]
[8, 0]
[8, 1]
[8, 2]
[8, 3]
[8, 9]
[9, 0]
[9, 1]
[9, 2]
[9, 3]
[9, 9]

Given a toroidal world of size [2, 2]
When I compute the neighborhood of [1,1] with radius 5
Then the result contains:
[0, 0]
[0, 1]
[1, 0]
[1, 1]

Given a toroidal world of size [10, 10, 10]
When I compute the neighborhood of [5, 5, 5] with radius 1
Then the result contains:
[4, 4, 4]
[4, 4, 5]
[4, 4, 6]
[4, 5, 4]
[4, 5, 5]
[4, 5, 6]
[4, 6, 4]
[4, 6, 5]
[4, 6, 6]
[5, 4, 4]
[5, 4, 5]
[5, 4, 6]
[5, 5, 4]
[5, 5, 5]
[5, 5, 6]
[5, 6, 4]
[5, 6, 5]
[5, 6, 6]
[6, 4, 4]
[6, 4, 5]
[6, 4, 6]
[6, 5, 4]
[6, 5, 5]
[6, 5, 6]
[6, 6, 4]
[6, 6, 5]
[6, 6, 6]