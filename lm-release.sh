#!/bin/bash
if [ $# -lt 2 ]
then
    echo "Usage: lm-release.sh <current version> <release version> [<new version>]"
    exit 1;
fi

# == Setup environnement ==
if [[ $OSTYPE == "darwin"* ]]; then
 SED='sed -i .orig '
 SEDE='sed -i .orig -E'
else
 SED='sed -i '
 SEDE='sed -i -r'
fi


CURRENT_VERSION=$1
RELEASE_VERSION=$2
NEXT_VERSION=$3

CURRENT_SNAPSHOT=$CURRENT_VERSION-SNAPSHOT
NEXT_SNAPSHOT=$NEXT_VERSION-SNAPSHOT


make_branch() {
    # Trouver tous les poms
    echo "============> Finding pom(s).xml       <=================================="
    poms=`find . -name pom.xml`

<<<<<<< HEAD
    git checkout master
    git checkout develop
=======
#    hg up develop
>>>>>>> adbf28d280e2f5ca91053c4f4e9005c617d5afca

    #== Passer à la version release (nouvelle branche)==

    git checkout -b release-$RELEASE_VERSION develop

    echo "============> Creating branch $RELEASE_VERSION <=========================="
    for pom in $poms; do $SED "/<parent>/,/<\/parent>/ s,<version>$CURRENT_SNAPSHOT<,<version>$RELEASE_VERSION<," $pom; done
    $SEDE "/<project/,/<properties>/ s,<version>$CURRENT_SNAPSHOT</version>,<version>$RELEASE_VERSION</version>," pom.xml

    git commit -a -m "Prepare release $RELEASE_VERSION"

    echo "============> Merging $CURRENT_VERSION branch into master branch <===================="

    git checkout master
    git merge --no-ff release-$RELEASE_VERSION
    git tag $RELEASE_VERSION

    echo "============> Moving back to $CURRENT_VERSION branch <===================="
    git checkout develop

    # == Préparer la branche snapshot suivante ==

    git merge --no-ff release-$RELEASE_VERSION

    if [ "x" != "x$3" ]; then
        echo "============> Creating new Snapshot branch $NEXT_SNAPSHOT <============"
        for pom in $poms; do $SED "/<parent>/,/<\/parent>/ s,<version>$RELEASE_VERSION<,<version>$NEXT_SNAPSHOT<," $pom; done
        $SEDE "/<project/,/<properties>/ s,<version>$RELEASE_VERSION</version>,<version>$NEXT_SNAPSHOT</version>," pom.xml
        git commit -a -m "Building new $NEXT_VERSION "
	    
    fi

    echo "NOW YOU NEED TO PUSH ALL BRANCHES, SUBMIT THE FOLLOWING COMMANDS :"
    echo "git push"
    echo "git checkout release-$RELEASE_VERSION"
    echo "git push origin release-$RELEASE_VERSION"
}

make_branch $@
