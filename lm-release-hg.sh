#!/bin/bash

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

    hg up develop

    #== Passer à la version release (nouvelle branche)==

    echo "============> Creating branch $RELEASE_VERSION <=========================="
    for pom in $poms; do $SED "/<parent>/,/<\/parent>/ s,<version>$CURRENT_SNAPSHOT<,<version>$RELEASE_VERSION<," $pom; done
    $SEDE "/<project/,/<properties>/ s,<version>$CURRENT_SNAPSHOT</version>,<version>$RELEASE_VERSION</version>," pom.xml

    hg branch release-$RELEASE_VERSION
    hg ci -m "Prepare release $RELEASE_VERSION"

    echo "============> Moving back to $CURRENT_VERSION branch <===================="
    hg up develop

    # == Préparer la branche snapshot suivante ==
    if [ "x" != "x$3" ]; then
        echo "============> Creating new Snapshot branch $CURRENT_SNAPSHOT <============"
        for pom in $poms; do $SED "/<parent>/,/<\/parent>/ s,<version>$CURRENT_SNAPSHOT<,<version>$NEXT_SNAPSHOT<," $pom; done
        $SEDE "/<project/,/<properties>/ s,<version>$CURRENT_SNAPSHOT</version>,<version>$NEXT_SNAPSHOT</version>," pom.xml
        hg ci -m "Building new $NEXT_VERSION "
	    
    fi
}

make_branch $@
