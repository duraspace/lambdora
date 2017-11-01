#!/bin/bash

###
# @author: Andrew Woods
# @since: 2017-10-31
#
# This script interactively exercises basic resource creation.
###

if [ -z $1 ] ; then
  echo "Please provide Fedora endpoint."
  exit
fi

BASE=$1

# Function to collect user input
function input() {
  if [ -z "$1+x" ]; then
    echo "input() requires a message arg!"
    exit
  fi

  MSG=$1
  read -p "$MSG" INPUT
  if [ "$INPUT" == "n" ]; then
    echo "Goodbye for now."
    exit
  fi
  echo "--------"
}

# Create container
input "Create an empty container? (y/n)"
LOC_CONTAINER=`curl -n -s -i -XPOST $BASE | grep Location | cut -f2 -d' ' | tr -d '\r'`
echo "Created: $LOC_CONTAINER" && echo

# View result
input "HEAD on new container? (y/n)"
curl -I $LOC_CONTAINER && echo

# Create a child container with RDF
input "Create a child container with RDF? (y/n)"
LOC_CHILD=`curl -s -i -XPOST -H"Content-Type: text/turtle" $LOC_CONTAINER --data-binary "
  <> <http://purl.org/dc/elements/1.1/title> 'A Title' ;
     <http://www.openarchives.org/ore/terms/proxyFor> <http://sweetclipart.com/multisite/sweetclipart/files/monkey_with_banana.png> ;
     <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.openarchives.org/ore/terms/Proxy> ." | 
  grep Location | cut -f2 -d' ' | tr -d '\r'`

echo "Created: $LOC_CHILD" && echo

# View result
input "HEAD on new child container? (y/n)"
curl -I $LOC_CHILD && echo

input "GET on new child container? (y/n)"
curl $LOC_CHILD && echo

# Create a child container with slug
input "Create a child container with slug?"
input "Provide slug: "
SLUG=$INPUT

LOC_CHILD_1=`curl -n -s -i -XPOST $LOC_CONTAINER -H"slug: $SLUG" | grep Location | cut -f2 -d' ' | tr -d '\r'`
echo "Created: $LOC_CHILD_1" && echo

# View result
input "GET on new slug child container? (y/n)"
curl $LOC_CHILD_1 && echo

# View parent container
input "GET on parent container? (y/n)"
curl $LOC_CONTAINER && echo
