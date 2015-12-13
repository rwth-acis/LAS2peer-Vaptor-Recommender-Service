#Vaptor Backend Component

##Recommender Service

It is part of Master Thesis "Adaptive Video Techniques for Informal Learning Support in Workplace Environments" at Chair of Computer Science 5, RWTH-Aachen.

This service is responsible to recommend the user, similar precompiled videos that corresponds to his search query. It compares the search query to the previous search queries done by the same or different users, and present the result as recommended videos. This will give the user a possibility to view videos that were compiled for a similar search query but with different user preferences. This service also gives user a list of related searches. These related searches are formulated by manipulating the search query. The result received by the user from the adapter service performs an AND operation on the terms in the search query. Therefore to make sure that user gets to see all those results that were skipped by the adapter service, this service query the annotation service with different combinations of the search query, and returns non empty results to the user.

Compile using ant, with "ant all". Then run bin/start_network.bat for windows or bin/start_network.sh for linux. 