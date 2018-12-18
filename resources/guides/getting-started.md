---
title: Getting Started
type: guide
description: This guide covers all the basics. Set up your first project and render some HTML.
icon: spaceship
index: 0
complete: true
---
This guide is intended to introduce programming beginners to making
websites, in particular static websites. It aims to assume little prior
knowledge except for the very basics of programming with Clojure.

> If you have any questions while following this guide please open an
> issue or join the `#perun` channel in the http://clojurians.net
> Slack group. We are happy to help.

## Step 1: Install Boot

[Boot][boot] is a tool to help develop Clojure projects. 
It downloads dependencies and can build your project. 
[Perun][perun] is built on top of Boot.

Installation instructions can be found in Boot's [Readme][boot-install]. 
Please make sure you're using the latest version of Boot (`boot -u`).

## Step 2: Create a new directory & add a `build.boot` file

> Note: This tutorial will assume a UNIX-like system (i.e. Linux/Mac), if you're using
> windows you can either look up the respective commands or try using the Explorer for basic tasks.
> Here are some basic intro links to working with a terminal:
> [Mac][terminal-basics-mac], [Linux][terminal-basics-linux], [Windows][terminal-basics-windows]

Create a directory:
```
mkdir my-website                         # creates the directory
cd my-website                            # moves you into the directory
```

and use your favorite text editor to add a file `build.boot` as below:

```clojure
(set-env!
 :source-paths #{"content"}
 :dependencies '[[perun "0.4.2-SNAPSHOT" :scope "test"]])

(require '[io.perun :as perun])
```

also add a file `boot.properties` (don't worry about it for now):

```sh
#http://boot-clj.com
#Mon Jan 18 23:19:36 CET 2016
BOOT_CLOJURE_NAME=org.clojure/clojure
BOOT_CLOJURE_VERSION=1.8.0
BOOT_VERSION=2.7.2
BOOT_EMIT_TARGET=no
```

## Step 3: The Simplest Thing Possible &trade;

Now let's create the simplest website possible: a single page with some text on it.

Create the directory `content` and place a file `index.markdown`, inside it, containing the following:

```markdown
# Hello World
We are making a website!
```

> Note: [Markdown][markdown] is a lightweight markup language with
> plain text formatting syntax designed so that it can be converted to
> [HTML][html] (among other formats).

Now Boot allows us to run tasks from the terminal. To see what tasks
we have at our disposal we can run `boot --help` from a terminal
session.

The printed output will have a section similar to this:
```
Tasks:   perun/asciidoctor           Parse asciidoc files with yaml front matter using Asciidoctor
         perun/asciidoctor*          Parse asciidoc files using Asciidoctor
         perun/assortment            Render multiple collections
         perun/atom-feed             Generate Atom feed
         perun/base                  Deprecated - metadata based on a files' path is now automatically set when other tasks
         perun/build-date            Add :date-build attribute to each file metadata and also to the global meta
         perun/canonical-url         Deprecated - The `:canonical-url` key will now automatically be set in the `entry` map passed
         perun/collection            Render single file for a collection of entries
         perun/draft                 Exclude draft files
         perun/global-metadata       Read global metadata from `perun.base.edn` or configured file.
         perun/gravatar              Find gravatar urls using emails
         perun/highlight             Syntax highlighting for code blocks using Pygments.
         perun/images-dimensions     Add images' dimensions to the file metadata:
         perun/images-resize         Resize images to the provided resolutions.
         perun/inject-scripts        Inject JavaScript scripts into html files.
         perun/markdown              Parse markdown files with yaml front matter
         perun/markdown*             Parse markdown files
         perun/mime-type             Adds `:mime-type` and `:file-type` keys to each file's metadata
         perun/paginate              Render multiple collections
         perun/pandoc                Parse files with pandoc
         perun/pandoc*               Parse files with pandoc
         perun/permalink             Moves a file so that its location matches the result of `permalink-fn`
         perun/print-meta            Utility task to print perun metadata
         perun/render                Render individual pages from input files
         perun/rss                   Generate RSS feed
         perun/sitemap               Generate sitemap
         perun/slug                  Renames a file so that the part before the extension matches the result of `slug-fn`
         perun/static                Render an individual page solely from a render function
         perun/tags                  Render multiple collections based on the `:tags` metadata key
         perun/ttr                   Calculate time to read for each file. Add `:ttr` key to the files' meta
         perun/word-count            Count words in each file. Add `:word-count` key to the files' meta
         perun/yaml-metadata         Parse YAML metadata at the beginning of files
```

The part below the `[...]` has been added to the set of available
tasks by the code we put into our `build.boot` file and are part of
[Perun][perun], the static site generator we will use in this guide.

> Perun works, similarly to — and on top of — Boot. Both provide tasks
> operating on a value and producing a new value. By ensuring that the
> newly produced value looks similarly to the received value you can
> mix and match tasks as you wish (this is often called composition).

Since we've just created a `index.markdown` file, let's try the `markdown` task:

```sh
$ boot perun/markdown
[yaml-metadata] - rendered new or changed file index.markdown
[markdown] - rendered new or changed file public/index.html
```

Great! We parsed a Markdown file. But where did the resulting
[HTML][html] go?  As mentioned earlier, Perun works by passing a value
from task to task. This value contains references to your files,
along with metadata about them. Perun's tasks either write files directly
or add metadata about your them to this value, so that your final site
can be progressively built up based on the input you provided.

```
┌───────┐            ┌───────┐            ┌───────┐            ┌───────┐
│ Value │   Task 1   │ Value │   Task 2   │ Value │   Task 3   │ Value │
│ V0    │───────────▶│ V1    │───────────▶│ V2    │───────────▶│ V3    │
│       │            │       │            │       │            │       │
└───────┘            └───────┘            └───────┘            └───────┘
```

To inspect the files and metadata that is passed from task to task, there are
two tasks we can use. The Boot built-in task `show` includes a convenient
option to display a tree of all files in the fileset. To see how a task changes
the fileset, you can use it like this:

```sh
$ boot show -f markdown show -f

└── index.markdown
[yaml-metadata] - rendered new or changed file index.markdown
[markdown] - rendered new or changed file public/index.html

└── public
    └── index.html
```

You can see here how the `markdown` task transformed `index.markdown` into
`public/index.html`. In the same way, you can use Perun's `print-meta` task to
see how the metadata changes. Below you can see how `print-meta` first prints a
map of metadata for `index.markdown`, and after the markdown task ran it prints
information for `index.html`.

```sh
$ boot perun/print-meta perun/markdown perun/print-meta
({:extension "markdown",
  :filename "index.markdown",
  :full-path "/Users/brent/.boot/cache/tmp/Users/brent/Dropbox/www/my-website/15nl/r3nb31/index.markdown",
  :parent-path "",
  :path "index.markdown",
  :permalink "/index.markdown",
  :short-filename "index",
  :slug "index"})
[yaml-metadata] - rendered new or changed file index.markdown
[markdown] - rendered new or changed file public/index.html
({:extension "html",
  :filename "index.html",
  :full-path "/Users/brent/.boot/cache/tmp/Users/brent/Dropbox/www/my-website/15nl/u2qisy/public/index.html",
  :include-atom true,
  :include-rss true,
  :original-path "index.markdown",
  :out-dir "public",
  :parent-path "public/",
  :path "public/index.html",
  :permalink "/",
  :short-filename "index",
  :slug "index",
  :io.perun/trace [:io.perun/yaml-metadata :io.perun/markdown]})
```

Let's do something with that information, let's turn it into a complete HTML file,
that includes our rendered Markdown. Perun provides a `render` task. Let's use
`boot` to figure out what it does:

```sh
$ boot perun/render --help
Render individual pages from input files

 The symbol supplied as `renderer` should resolve to a function
 which will be called with a map containing the following keys:
  - `:meta`, global perun metadata
  - `:entries`, all entries
  - `:entry`, the entry to be rendered

 Entries can optionally be filtered by supplying a function
 to the `filterer` option.

Options:
  -h, --help                   Print this help info.
  -o, --out-dir OUTDIR         OUTDIR sets the output directory (default: "public").
      --filterer FILTER        FILTER sets predicate to use for selecting entries (default: `identity`).
  -e, --extensions EXTENSIONS  Conj EXTENSIONS onto extensions of files to include
  -r, --renderer RENDERER      RENDERER sets page renderer (fully qualified symbol which resolves to a function).
  -m, --meta META              META sets metadata to set on each entry.
```

Ok, so it takes input files and renders individual pages. Given that we only have
a single HTML file right now that sounds like what we want. Let's try just calling
the `render` task after the `markdown` task:

```
$ boot markdown render
                              java.lang.Thread.run              Thread.java:  745
java.util.concurrent.ThreadPoolExecutor$Worker.run  ThreadPoolExecutor.java:  617
 java.util.concurrent.ThreadPoolExecutor.runWorker  ThreadPoolExecutor.java: 1142
               java.util.concurrent.FutureTask.run          FutureTask.java:  266
                                               ...
               clojure.core/binding-conveyor-fn/fn                 core.clj: 1938
                                 boot.core/boot/fn                 core.clj: 1030
                                               ...
                         boot.core/construct-tasks                 core.clj:  992
                                clojure.core/apply                 core.clj:  646
                                               ...
                              io.perun/eval1031/fn                perun.clj:  749
                          io.perun/render-pre-wrap                perun.clj:  699
                          io.perun/assert-renderer                perun.clj:  689
  java.lang.AssertionError: Assert failed: Renderer must be a fully qualified symbol, i.e. 'my.ns/fun
                            (and (symbol? sym) (namespace sym))
clojure.lang.ExceptionInfo: Assert failed: Renderer must be a fully qualified symbol, i.e. 'my.ns/fun
                            (and (symbol? sym) (namespace sym))
    file: "/var/folders/n4/vg52wwmn02xbfx_4g2l53z1m0000gn/T/boot.user8340544814703936458.clj"
    line: 11
```

Duh, that didn't work. The error tells us we need to supply a
(fully-qualified) symbol to the `renderer` option pointing to a
function. Let's create a namespace with a renderer function for our
page. First create the required directory structure:

```sh
mkdir -p src/site
```

Now add a file at `src/site/core.clj` containing the following:

```clojure
(ns site.core
  (:require [hiccup.page :as hp]))

(defn page [data]
  (hp/html5
   [:div {:style "max-width: 900px; margin: 40px auto;"}
    (-> data :entry :content)]))
```

We've added a function that renders a bit of HTML and inserts what has
previously been parsed by the `markdown` task (the `:content`). The
rendering is done by [Hiccup][hiccup] a library to convert Clojure
data structures to HTML. A simplistic example would be:

```
[:span {:class "foo"} "bar"]    ; Clojure produces
<span class="foo">bar</span>    ; HTML
```

Now that we have a function that we can use as `renderer` let's give it a try:

```sh
$ boot perun/markdown perun/render -r site.core/page  # -r is a shorthand for --renderer
```

**Error!** Our program can't find our function because we didn't tell Boot
to look in the `src` directory for our code. Also, it wouldn't be able
to execute the Hiccup code because we haven't added it to our list of
dependencies. Modify `build.boot` so it looks like this:

```clojure
(set-env!
 :source-paths #{"src" "content"}
 :dependencies '[[perun "0.4.2-SNAPSHOT" :scope "test"]
                 [hiccup "1.0.5" :exclusions [org.clojure/clojure]]])

(require '[io.perun :as perun])
```

> Note: By adding a dependency to the list of `:dependencies` in `build.boot`
> you make it available to the rest of your program.

Now try the command from above again and see that it works:

```sh
$ boot perun/markdown perun/render -r site.core/page
[yaml-metadata] - rendered new or changed file index.markdown
[markdown] - rendered new or changed file public/index.html
[render] - rendered new or changed file public/index.html
```

Still, we don't see any files being generated, to fix that just append
the `target` task to your command:

```sh
$ boot perun/markdown perun/render -r site.core/page target
[yaml-metadata] - rendered new or changed file index.markdown
[markdown] - rendered new or changed file public/index.html
[render] - rendered new or changed file public/index.html
Writing target dir(s)...
```

> Note: Remember the value we spoke about earlier that is passed from
> task to task? In Boot this value describes a directory structure and
> files inside it. Whenever the `target` task is used, Boot will sync
> relevant files from this description to an actual target directory.

Now there should be a directory called `target` containing another
directory `public`, finally containing a file `index.html`.
If you open that file you should see your new website! :tada:

Admittedly it is a very basic website but let's recap what we've done:

1. Instead of writing our complete website in HTML we only define its
   "frame" or layout in Hiccup/HTML. For the actual content of our
   website we use a specific language (Markdown) making it much easier
   to write and edit content.
1. Because an HTML file is generated for each Markdown file it's trivial to add new pages.

## Step 4: Adding more pages and a real server

Let's add an `about` page by adding the file `content/about.markdown`:

```markdown
# About this site
This site has been made by following the Perun guides
```

Now so that our visitors can find our new about page let's change our
`index.markdown` file to look like this:

```markdown
# Hello World
We are making a website! ([about this website](/about.html))
```

After rebuilding our site by running `boot markdown render -r
site.core/page target` we can open `target/public/index.html` again
and see that there is a link to our new about page. If we click it
however there will be an error.

This is because currently we're just opening those files from our
filesystem and not retrieving them from a server as it's usually done
with websites. To get closer to the "mode of operation" of an actual
website and to fix this problem we need to serve our website over
[HTTP][http].

Add `[pandeiro/boot-http "0.8.3" :exclusions [org.clojure/clojure]]`
to the list of `:dependencies` in your `build.boot`. Also modify the
`require` statement in that file to look like this:

```clojure
(require '[io.perun :refer :all]
         '[pandeiro.boot-http :refer [serve]])
```

Now use the newly available `serve` task like this:

```
boot serve --resource-root public perun/markdown perun/render -r site.core/page wait
```

There are two new things here:

1. `serve --resource-root public` — The `serve` task starts an HTTP
    server and serves files from the JAVA classpath, which we can
    think of as an imaginary directory somewhere on our computer
    containing lots of files. When we previously used the `target`
    task we saw that our generated files were all in a directory
    `public` so we tell the server to only respond to requests for
    files in `public`. (We could have also used the shorthand `-r`
    option instead of `--resource-root` by the way.)
2. `wait` tells the task pipeline to wait even after it has finished.
    This way we ensure that even after all files are generated the server
    will keep running to serve those files.

Now after running this command there will be a line printed like this one:
```
Started Jetty on http://localhost:3000
```
Go to [http://localhost:3000](http://localhost:3000) — you should see
the index.html file with the link to your about page. Clicking on the
link will bring you to
[http://localhost:3000/about.html](http://localhost:3000/about.html)
properly displaying your about page.

**Success!** Every good website should have a way to go back to its
homepage however so let's modify our renderer function to always show
a link to the homepage ("root") of the site. 
In `src/site/core.clj` change the `page` function to look like this

```clojure
(defn page [data]
  (hp/html5
   [:div {:style "max-width: 900px; margin: 0 auto;"}
    [:a {:href "/"} "Home"] ; <---- We added this
    (-> data :entry :content)]))
```

After restarting our site building & serving command go to
[http://localhost:3000](http://localhost:3000) again and view your
site. On both pages there should now be a little "Home" link at the
top bringing you back to the index page.

**One more thing before you're done:** Currently every time we make a
change we have to restart our command. To avoid this you can adapt the
command like this:

```sh
boot serve -r public watch perun/markdown perun/render -r site.core/page
```
The `watch` task will rebuild your page whenever an important file
changes. (Because the watch tasks keeps the pipeline running we don't
need `wait` any longer.) **Give it a try by editing `index.markdown`
and reloading the browser!**

## This is it!

You're at the end of the "Getting Started" tutorial. There are many
things still to be explored, proceed with whatever interests you most:

- **Make things pretty.** Admittedly our site isn't very pretty right
  now. [CSS][css] can be used to influence the appearance of HTML. 
  It can color texts and backgrounds and do lot of other
  things. If you're familiar with CSS you might already know how to
  add more CSS to the page, if not you can learn all about CSS in the
  [excellent material created by CSSClasses][cssclasses-guide].
- **Write more content.** Perhaps the content on our new site could
  also be expanded. Adding lists, images and code snippets is trivial
  with Markdown. You can learn more about Markdown in
  [Github's excellent guides][md-guide].
- Remembering and typing the long `boot serve ...` command can be
  annoying, learn how you can define your own tasks consisting of
  several sub-tasks in the [Boot Task Guide][task-guide]
- **Make a blog.** Everyone can have a [blog][blog], they're a good
  way to keep friends updated, talk about technical problems and their
  solutions or write about whatever you want. If you'd like to add a
  blog to your new site or are just curious how that would work
  [[check out the Blog Guide|Make a Blog]].
- **Add custom content beyond Markdown.** Sometimes instead of texts
  you want to have a page rendering other kinds of data. A list of
  colleagues and pictures of their faces for instance. Markdown is
  not ideal for these use cases. To learn what you can do in these
  case [[check out the Beyond-Markdown Guide|Beyond Markdown]].
- **Deploy your site.** You now have a website, great! But it's not on
  the internet and so you can't show it to any of your friends. To
  learn more about how to deploy your website
  [[check out the Deployment Guide|Deployment]].

[boot]: https://github.com/boot-clj/boot
[boot-install]: https://github.com/boot-clj/boot#install
[terminal-basics-mac]: http://mac.appstorm.net/how-to/utilities-how-to/how-to-use-terminal-the-basics/
[terminal-basics-linux]: http://community.linuxmint.com/tutorial/view/100
[terminal-basics-windows]: http://www.cs.princeton.edu/courses/archive/spr05/cos126/cmd-prompt.html
[perun]: https://github.com/hashobject/perun
[markdown]: https://en.wikipedia.org/wiki/Markdown
[html]: https://en.wikipedia.org/wiki/HTML
[http]: https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol
[hiccup]: https://github.com/weavejester/hiccup
[css]: https://en.wikipedia.org/wiki/Cascading_Style_Sheets
[cssclasses-guide]: http://cssclasses.cssconf.eu/materials/#css
[md-guide]: https://guides.github.com/features/mastering-markdown/
[blog]: https://en.wikipedia.org/wiki/Blog
