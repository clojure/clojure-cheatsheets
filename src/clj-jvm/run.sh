#! /bin/bash

CMD="lein run"

set -x

# 3 choices for links: none, to clojure.github.org, or to
# clojuredocs.org:

#LINK_TARGET=nolinks
#LINK_TARGET=links-to-clojure
#LINK_TARGET=links-to-clojuredocs
LINK_TARGET=links-to-grimoire

TOOLTIPS=no-tooltips
#TOOLTIPS=use-title-attribute
#TOOLTIPS=tiptip

CLOJUREDOCS_SNAPSHOT=""
#CLOJUREDOCS_SNAPSHOT="$clojuredocs-snapshot.edn"

# Optionally produce PDF files by running LaTeX.  See README.markdown
# for notes on what parts of LaTeX are enough for this to work.

PRODUCE_PDF="no"
#PRODUCE_PDF="yes"

######################################################################
# Make embeddable version for clojure.org/cheatsheet
######################################################################
echo "Generating embeddable version for clojure.org/cheatsheet ..."
${CMD} links-to-clojuredocs use-title-attribute
EXIT_STATUS=$?

if [ ${EXIT_STATUS} != 0 ]
then
    echo "Exit status ${EXIT_STATUS} from ${CMD}"
    exit ${EXIT_STATUS}
fi
/bin/mv cheatsheet-embeddable.html cheatsheet-embeddable-for-clojure.org.html

if [ ${PRODUCE_PDF} == "yes" ]
then
    for PAPER in a4 usletter
    do
	for COLOR in color grey bw
	do
	    BASENAME="cheatsheet-${PAPER}-${COLOR}"
	    latex ${BASENAME}
	    dvipdfm ${BASENAME}
	    
            # Clean up some files created by latex
	    /bin/rm -f ${BASENAME}.aux ${BASENAME}.dvi ${BASENAME}.log ${BASENAME}.out
	done
    done
    /bin/mv *.pdf ../../pdf
fi

# Useful to uncomment this, to generate LaTeX and PDF only, for faster
# iteration on problems with LaTeX files.
#exit 0

######################################################################
# Make multiple full versions for those who prefer something else,
# e.g. no tooltips.
######################################################################
for TOOLTIPS in tiptip use-title-attribute no-tooltips
do
    for CDOCS_SUMMARY in cdocs-summary no-cdocs-summary
    do
	case "${CDOCS_SUMMARY}" in
	no-cdocs-summary) CLOJUREDOCS_SNAPSHOT=""
	                  ;;
	cdocs-summary) CLOJUREDOCS_SNAPSHOT="clojuredocs-snapshot.edn"
	                  ;;
	esac
	TARGET="cheatsheet-${TOOLTIPS}-${CDOCS_SUMMARY}.html"
	echo "Generating ${TARGET} ..."
	${CMD} ${LINK_TARGET} ${TOOLTIPS} ${CLOJUREDOCS_SNAPSHOT}
	EXIT_STATUS=$?

	if [ ${EXIT_STATUS} != 0 ]
	then
	    echo "Exit status ${EXIT_STATUS} from ${CMD}"
	    exit ${EXIT_STATUS}
	fi
	/bin/mv cheatsheet-full.html cheatsheet-${TOOLTIPS}-${CDOCS_SUMMARY}.html
	# Uncomment following line if you want to test new changes
	# with generating only the first variant of the cheatsheet.
	#exit 0
    done
done
