# Clojure/Java cheat sheet generator

The program `src/generator/core.clj` and accompanying shell script
`run.sh` can generate HTML and LaTeX versions of the Clojure/Java
cheat sheet.  A suitable LaTeX installation on your computer can then
be used to generate PDF files as well.  They are all generated with
structure and symbols specified in the value of `cheatsheet-structure`
in the Clojure source file.  They contain clickable links to the
documentation on either [clojure.github.com][clojure github] or
[clojuredocs.org][clojuredocs] (or no links at all).

[clojure github]: http://clojure.github.com
[clojuredocs]: http://clojuredocs.org

The cheat sheet generated is currently specific to Clojure versions
1.3 through 1.5.1, with new symbols added in Clojure 1.4 and 1.5
marked with "(1.4)" or "(1.5)".  At some point it will likely be
easier to simply change this program as desired for later versions of
Clojure, rather than try to have the same program able to generate
cheatsheets for too many Clojure versions.

# Generating cheat sheet files

Edit `run.sh` to specify the values of `LINK_TARGET` and `PRODUCE_PDF`
variables to your liking.  If you want to produce PDF files, you must
have a suitable LaTeX installation on your computer.  See "LaTeX
installation notes" below for what is needed.

Run this command:

    ./run.sh

Output files are:

* `cheatsheet-no-tooltips-no-cdocs-summary.html`
* `cheatsheet-tiptip-cdocs-summary.html`
* `cheatsheet-tiptip-no-cdocs-summary.html`
* `cheatsheet-use-title-attribute-cdocs-summary.html`
* `cheatsheet-use-title-attribute-no-cdocs-summary.html`

    Five different full versions of the standalone HTML files are
    generated, plus one "embeddable" version (see below).  The full
    versions are standalone HTML files that are useful for viewing
    locally with a web browser while testing changes to the program,
    or for publishing on the web.  Several contain links to files in
    the cheatsheet_files subdirectory, and those must also be
    published.

    They differ only in whether they have tooltip text on the Clojure
    symbols or not, and if so, whether those use HTML-standard title
    attribute text or a JavaScript-based method using TipTip and
    jQuery.  Another variation between tooltip-enhanced versions are
    whether the tooltip text includes only the doc string for the
    symbol, or in addition a 1- or 2-line summary of how many examples
    and comments exist for the symbol on ClojureDocs.org.

* `cheatsheet-embeddable-for-clojure.org.html`

    The embeddable version is almost the same, except only
    includes those things needed for publishing easily at
    [http://clojure.org/cheatsheet][cheatsheet].

[cheatsheet]: http://clojure.org/cheatsheet

* `warnings.log`

    Warning messages about any symbols for which no links to
    documentation could be found in the internal map called
    `symbol-name-to-url`.  Also a list of all symbols that are in that
    map which are not mentioned in the cheat sheet.  These may be
    useful to add to future revisions of the cheat sheet, if they are
    considered important enough.

* `cheatsheet-usletter-grey.tex`
* `cheatsheet-usletter-color.tex`
* `cheatsheet-usletter-bw.tex`
* `cheatsheet-a4-grey.tex`
* `cheatsheet-a4-color.tex`
* `cheatsheet-a4-bw.tex`

    LaTeX source files for black & white (bw), grayscale, and color
    versions of the cheat sheet, on either A4 or US letter size paper.
    The font size has been tuned by hand so that everything fits onto
    2 pages on either size of paper.  Search for `fontsize` in the
    Clojure source file if future modifications to the cheat sheet
    warrant further modification of these.

If you enable it in `run.sh`, corresponding PDF files will also be
generated for each of the LaTeX files.


Things still missing:

* No footer with version number, date, and attributions at the bottom.


# LaTeX installation notes

On a Mac with MacPorts 2.0.3, the following two packages, plus what
they depend upon, are sufficient:

* texlive-latex-recommended (needed for scrreprt.cls)
* texlive-fonts-recommended (needed for the lmodern font)

This command will install these packages:

    % sudo port install texlive-latex-recommended texlive-fonts-recommended


On an Ubuntu 64-bit 10.4 LTS system, I got good results after
installing the following packages:

* texlive-latex-base
* texlive-latex-extra


## License

Copyright (C) 2012-2013 Andy Fingerhut

Distributed under the Eclipse Public License, the same as Clojure.
