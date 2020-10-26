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


# Installation

## For Ubuntu 18.04 Linux

Starting from a minimal Ubuntu 18.04 Linux installation, here is what
you need to install in order to run the cheatsheet generator.

+ Some version of the JDK, e.g. OpenJDK 11 can be installed via the
  command: `sudo apt-get install default-jdk`
+ Leiningen.  The `lein` bash script available from
  https://leiningen.org is enough here.

The above is sufficient if you are not generating the PDF versions of
the cheatsheet.  If you want to generate the PDF versions, you also
need to install these LaTeX packages:

+ `sudo apt-get install texlive-latex-base texlive-latex-extra`


## Mac OS X

As above, you must also install some version of the JDK and Leiningen.

On a Mac with MacPorts 2.0.3, the following two packages, plus what
they depend upon, are sufficient for generating PDF files:

* `texlive-latex-recommended` (needed for scrreprt.cls)
* `texlive-fonts-recommended` (needed for the lmodern font)

This command will install these packages:

```bash
sudo port install texlive-latex-recommended texlive-fonts-recommended
```


# Generating cheat sheet files

## Getting an updated copy of the contents of the ClojureDocs web site

```bash
curl -O https://clojuredocs.org/clojuredocs-export.json
```

## Running the cheatsheet generator

Edit `run.sh` to specify the values of `LINK_TARGET` and `PRODUCE_PDF`
variables to your liking.  If you want to produce PDF files, you must
have a suitable LaTeX installation on your system (see Installation
above).

Run this command:

```bash
./run.sh
```

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


## License

Copyright (C) 2012-2020 Andy Fingerhut

Distributed under the Eclipse Public License, the same as Clojure.
