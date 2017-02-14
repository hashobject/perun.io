---
title: Built-Ins
type: guide
description: Learn about all the tasks coming with Perun by default. Render markdown, create Atom feeds, sitemaps and more.
icon: design
index: 1
complete: true
---
Perun's built-in tasks are designed to provide the most commonly-needed functionality
for building static sites. They were written with flexibility in mind, and can be
customized in many different ways. Let's take an in-depth look at them.

## Content Tasks

Content tasks create new files, or modify existing ones, and they form the backbone
of any Perun-based static site.

Conveniently, Perun's content tasks are reload-enabled, so if you set up your `boot`
pipeline with the `watch` task, changes to your render functions or input files will
cause the pipeline to re-run with your new changes in effect. When used in
conjunction with a frontend reloader, like `boot-reload` or `boot-livereload`, you
get automatic hot reloading in your browser just by saving a file.

In addition to responding to changes, Perun also knows what stays the same from one
`watch` loop to the next. If the inputs for an output file haven't changed, Perun
will reuse the results from the last loop, which minimizes work and makes your
feedback loop as fast as possible.

-----

### markdown

If you plan to write text content and haven't already chosen a format, Perun
recommends [Markdown][markdown]. It's a flexible format that can be transformed
into many different output formats, depending on your needs. Perun currently only
supports HTML output, but expanding this is planned for the future.

Basic usage is simply `(markdown)`. This invocation will look for files that end
with ".md" or ".markdown", parse their contents into HTML, and write new files to
the "public" directory that have the same name as the originals, but with the
extension changed to ".html".

In addition to parsing Markdown, `markdown` will also look for YAML frontmatter at
the beginning of your file, and add the data it finds there to the file's metadata,
so that it can be used by later tasks. For example, if you wanted to note the author,
the date an article was published, and some tags, you would put something like this
at the head of your markdown file:

```yaml
---
author: Joe Shmoe
date-published: 2017-02-02
tags:
 - interesting
 - stuff
---
```

The data that you can put here is not limited to things that Perun will use - if you
want to have custom data associated with this file, this is one convenient way to do
it.

You can customize `markdown` using these options:

- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:meta` --- key/values set in this map will be set in the metadata of each file
  processed
- `:options` --- Enable or disable Markdown extensions by passing a map of extensions
  to true/false, eg. `{:extensions {:smarts true}`. Valid extension keys are\*:

    - `:smarts` --- Pretty ellipses, dashes and apostrophes
    - `:quotes` --- Pretty single and double quotes
    - `:smartypants` --- All of the smartypants prettyfications. Equivalent to `:smarts` +
                         `:quotes`
    - `:abbreviations` --- PHP Markdown Extra style abbreviations
    - `:hardwraps` --- Enables the parsing of hard wraps as HTML linebreaks. Similar to
                       what github does
    - `:autolinks` --- Enables plain autolinks the way github flavoured markdown implements
                       them. With this extension enabled pegdown will intelligently
                       recognize URLs and email addresses without any further delimiters
                       and mark them as the respective link type.
    - `:tables` --- Table support similar to what Multimarkdown offers
    - `:definitions` --- PHP Markdown Extra style definition lists
    - `:fenced-code-blocks` --- PHP Markdown Extra style fenced code blocks
    - `:wikilinks` --- Support [[Wiki-style links]]
    - `:strikethrough` --- Support ~~strikethroughs~~ as supported in Pandoc and Github
    - `:anchorlinks` --- Enables anchor links in headers
    - `:suppress-html-blocks` --- Suppresses HTML blocks. They will be accepted in the
                                  input but not be contained in the output
    - `:supress-all-html` --- Suppresses HTML blocks as well as inline HTML tags. Both
                              will be accepted in the input but not be contained in
                              the output
    - `:forcelistitempara` --- Force List and Definition Paragraph wrapping if it
                               includes more than just a single paragraph; primarily
                               for backwards-compatibility
    - `:atxheaderspace` --- Requires a space char after Atx # header prefixes, so that
                            #dasdsdaf is not a header
    - `:relaxedhrules` --- Allow horizontal rules without a blank line following them
    - `:tasklistitems` --- GitHub style task list items: - [ ] and - [x]
    - `:extanchorlinks` --- Generate anchor links for headers using complete contents of
                            the header. Spaces and non-alphanumerics replaced by `-`,
                            multiple dashes trimmed to one. Anchor link is added as first
                            element inside the header with empty content:
                            `<h1><a name="header-a"></a>header a</h1>`
    - `:all` --- All available extensions excluding the `:suppress-*` extensions
    - `:all-optionals` --- `:atxheaderspace` + `:relaxedhrules` + `:tasklistitems` +
                           `:extanchorlinks`
    - `:all-with-optionals` --- `:all` + `:atxheaderspace` + `:relaxedhrules` +
                                `:tasklistitems`

By default, the `:autolinks`, `:strikethrough`, `:fenced-code-blocks`, and
`:extanchorlinks` extensions are enabled.

-----

### render

The `render` task allows you to customize HTML output using a function that you define,
using an existing file as input. Basic usage is `(render :renderer 'your.ns/a-render-fn)`.
Options are:

- `:renderer` --- This is a symbol that resolves to a function you've defined. It will
  be called with a map containing the following keys:
    - `:meta`, global Perun metadata
    - `:entries`, all entries that will be rendered by this `render` call
    - `:entry`, the entry to be rendered
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:filterer` --- restrict the files that will be processed using this function, which
will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:meta` --- key/values set in this map will be set in the metadata of each file
  processed

-----

### static

If you want to render a page based solely on Clojure code, this is the task for you.
`static` is just like `render`, except that it does not require an input file. Basic
usage is `(static :renderer 'your.ns/a-render-fn)`. All options are:

- `:renderer` --- This is a symbol that resolves to a function you've defined. It will
  be called with a map containing the following keys:
    - `:meta`, global Perun metadata
    - `:entry`, the entry to be rendered
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:page` --- the path of the page that will be written
- `:meta` --- key/values set in this map will be set in the metadata of the output file

-----

### collection

Whereas `render` and `static` are for producing pages that stand on their own, so to
speak, `collection` is for producing output that aggregates a number of pages. For
instance, if you want to list your recent blog posts, or provide a table of contents,
then `collection` should meet your needs. Basic usage is `(collection :renderer
'your.ns/a-render-fn)`, and all options are as follows:

- `:renderer` --- This is a symbol that resolves to a function you've defined. It will
  be called with a map containing the following keys:
    - `:meta`, global Perun metadata
    - `:entries`, all entries included in this collection
    - `:entry`, the entry to be rendered
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:sortby` --- `entries` will be sorted by the values returned from this function
- `:comparator` --- values returned from `:sortby` will be compared using this function
  during sorting.
- `:page` --- the path of the page that will be written
- `:meta` --- key/values set in this map will be set in the metadata of the output file

-----

### paginate

You'll reach for `paginate` if you don't want to squeeze all of your content into a
single collection, but instead would like to have your entries divided up into several
pages. Your render function will be called for each of the resulting pages, and the
`:entry` will contain a `:page` key, so your render function will be able to tell which
page it is rendering. `(paginate :renderer 'your.ns/a-render-fn)` is basic usage, and
options are:

- `:renderer` --- This is a symbol that resolves to a function you've defined. It will
  be called with a map containing the following keys:
    - `:meta`, global Perun metadata
    - `:entries`, all entries that will be rendered by this `render` call
    - `:entry`, the entry to be rendered
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:prefix` --- This prefix will be concatenated with the page number to produce a
  filename for each page
- `:page-size` --- The input files to `paginate` will be partitioned into groups of
  this size
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:sortby` --- `entries` will be sorted by the values returned from this function
- `:comparator` --- values returned from `:sortby` will be compared using this function
  during sorting.
- `:meta` --- key/values set in this map will be set in the metadata of the output files

-----

### tags

The `tags` task takes care of automatically generating a dedicated page for each unique
tag you list on its input files. In most use-cases the `:tags` metadata will be set in
the YAML header of your content files, like so:

```yaml
---
:tags
 - tag1
 - tag2
---
```

`tags` will read this metadata, group all input by each tag, and create a collection for
each tag. When your render function is called, the `:entry` will have the `:tag` key set,
indicating which tag the `:entries` belong to. Basic usage is `(tags :renderer
'your.ns/a-render-fn)`, and the options are:

- `:renderer` --- This is a symbol that resolves to a function you've defined. It will
  be called with a map containing the following keys:
    - `:meta`, global Perun metadata
    - `:entries`, all entries that have this tag
    - `:entry`, the entry to be rendered
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:sortby` --- `entries` will be sorted by the values returned from this function
- `:comparator` --- values returned from `:sortby` will be compared using this function
  during sorting.
- `:meta` --- key/values set in this map will be set in the metadata of the output files

-----

### assortment

If you have needs that aren't met by the rendering functions above, chances are that you
can customize `assortment` to do what you're looking for. It will render multiple
collections like `paginate` and `tags` do (in fact those tasks are specialized versions
of `assortment`), but what those pages will be is entirely up to you.

The key here is the custom `:grouper` function. `assortment` will call `:grouper` with
a seq of entries, and `:grouper` must return a map, where the keys are the paths of files
to be written, and the values are inputs to your `:renderer`. By convention, Perun calls
render functions with a map containing `:entry`, `:entries`, or `:meta` keys, but you
are under no obligation to follow suit. As long as `:renderer` knows what to do with the
values of `:grouper`'s return, everything should work out fine.

Basic usage will be `(assortment :grouper a-grouping-fn :renderer 'your.ns/a-render-fn)`,
and the following options are accepted:

- `:grouper` --- This function takes a seq of `entries` and returns a map with keys
  that are paths to be written, and values that are inputs to the function named by
  `:renderer`
- `:renderer` --- This is a symbol that resolves to a function you've defined. It will
  be called once for each value of the map returned by `:grouper`
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:sortby` --- `entries` will be sorted by the values returned from this function
- `:comparator` --- values returned from `:sortby` will be compared using this function
  during sorting.
- `:meta` --- key/values set in this map will be set in the metadata of the output files

-----

### inject-scripts

`inject-scripts` is useful if you have javascript that should be included on many pages,
but you don't want to include it in your render functions. For example, analytics
javascript probably shouldn't be loaded in development, but should be loaded in
production. Rather than writing logic to achieve this behavior, `inject-scripts` can
simply be included in your production pipeline, but absent from development. Usage:
`(inject-scripts :scripts #{"path/to/script.js"})`, and all options:

- `:scripts` --- files named here will be read and their contents injected before the
  `<body>` tag of the HTML files that are processed by this task.
- `:filter` --- regexes in this set will be used to select file paths to process
- `:remove` --- regexes in this set will be used to remove file paths to process
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

### markdown*

The `markdown` task does two main things, and is thus composed of two simpler tasks.
`markdown*` is one of those tasks; it parses Markdown, but does not parse the YAML
metadata at the head of the file. Most Perunians won't need to use `markdown*`, but
it is available if you need to customize Markdown processing in some way. Its options
are the same as `markdown`'s.

-----

### yaml-metadata

The other task that comprises `markdown` is `yaml-metadata`. It only parses the YAML
frontmatter at the head of files. and overwrites the file without the YAML header, for
further processing. Again, this is of limited usefulness for most use-cases, but in
the future, content tasks for formats besides Markdown will be able to use `yaml-metadata`
in the same way. It has no required arguments and takes two of Perun's more common options:

- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

## Metadata Tasks

While Content Tasks are for modifying existing or writing new files on disk, Metadata
Tasks leave content alone, and only tell us something about our files as they already
are.

-----

### global-metadata

Global metadata is data that applies to your whole site, like its `:base-url`, or the
`:doc-root` for your files, or the site's `:site-title`. This task reads an edn file
containing values like this, and makes those values accessible to later tasks. Basic usage
is `(global-metadata)`, and its only option is:

- `:filename` --- The name of the edn file to read, `"perun.base.edn"` by default

-----

### build-date

It can be useful to know how old the file you're looking at is, so `build-date` populates
the `:date-build` key on each file's metadata with a timestamp for the current build.
Default behavior is achieved with `(build-date)`, and you can pass these options:

- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

### gravatar

Especially handy if you have multiple authors on your site, `gravatar` performs an API
request to `gravatar.com` and retrieves a url for the avatar associated with an email
address. This url is then stored under the key of your choice in each file's metadata.
Basic usage is `(gravatar :source-key :author-email :target-key :gravatar)`, and you
can customize the task with these options:

- `:source-key` --- The file metadata key to read the email address from
- `:target-key` --- The file metadata key to write the gravatar url to
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

### ttr

It's polite to tell your readers how much time it will probably take them to read your
content. `ttr` (for "time to read") estimates the amount of reading time in minutes
based on the length of your content, and stores the result in the `:ttr` metadata key.
Basic usage is `(ttr)`, and you can control which files to consider using these options:

- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

### word-count

Would you believe that this task counts words? I hope so, because that's what it does.
Basic usage is `(word-count)`, and you can control which files get counted using:

- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

### mime-type

If you'd like Perun to guess the mime types of your files, use `mime-type`. It's not
guaranteed to be 100% accurate, but if your files have sensible extensions, it knows
what to do. `(mime-type)` will guess all of your files, and you can limit this with
these options:

- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

## Moving and Deleting Tasks

These tasks aren't concerned with your files' content, or their metadata, they just
want those files to go where they belong. We should mention that you don't _have_
to use these tasks to move or delete files. If you have a preferred way of doing
these things with Boot, you are free to do that --- Perun will adapt to your way of
doing things.

-----

### draft

You can prevent your in-progress content from accidentally being published by using
the `draft` task. If you set `:draft` to `true` on a file's metadata, and then call
`(draft)`, that file will be removed from the fileset. You can use this in your
production deployment task to exclude content whose time has not yet come. There are
no options.

-----

### slug

Renaming a file is easy with `slug`. Provide a `:slug-fn` that takes global metadata
and file metadata as arguments, and returns a filename minus extension, and the file
shall be moved. The default `:slug-fn` parses the date out of Jekyll-style filenames (aka,
`YYYY-MM-DD-slug.ext` -> `slug.ext`). If that's what you want for all your files, then
`(slug)` will do. You can also customize it with these options:

- `:slug-fn` --- A function of two arguments: global metadata, and one file's metadata.
  Returns a new name for the file.
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

### permalink

If you'd like to think about URL's instead of filenames, then use `permalink`. It will
intelligently handle URL's for files as well as folders (aka, links that end with a
slash). The default `:permalink-fn` strips the `:doc-root` out of the file's `:path`
and uses what remains as the permalink. If that meets your needs, the invocation will
be `(permalink)`. Customization is achieved by way of these options:

- `:permalink-fn` --- A function of two arguments: global metadata, and one file's metadata.
  Returns a new permalink for the file; may end in a slash.
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`

-----

## Specialty Collections

These tasks aggregate your content in much the same way that `collection` does, but they
don't give you control over their output, because they are each intended to achieve a
specific task.

-----

### atom-feed

As you may have guessed, `atom-feed` produces a file following the Atom spec. Basic usage,
assuming you have site-specific metadata set globally, is `(atom-feed)`. Otherwise, site
information must be passed to the task. Options are as follows:

- `:filename` --- Atom XML will be written to this file
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:site-title` --- overrides global metadata of the same name; at least one is required
  to be set
- `:base-url` --- must end in a slash. Overrides global metadata of the same name; at
  least one is required to be set
- `:description` --- overrides global metadata of the same name; optional

-----

### rss

As an alternative to `atom-feed`, `rss` writes a file following the RSS spec. Invocation
is similarly just `(rss)`, if the global metadata contains site information. If it does not,
it must be set by the task. Options are:

- `:filename` --- RSS XML will be written to this file
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:site-title` --- overrides global metadata of the same name; at least one is required
  to be set
- `:base-url` --- must end in a slash. Overrides global metadata of the same name; at
  least one is required to be set
- `:description` --- overrides global metadata of the same name; optional

-----

### sitemap

Sitemaps are intended to make your site more easily crawlable by search engines. Since Perun
knows about every page in your site, it can generate one for you automatically, using
`(sitemap)`, by default. You can also pass any of these options:

- `:filename` --- Sitemap XML will be written to this file
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"

-----

## Image Tasks

Images are an important part of many a static site, so Perun provides a few convenience
tasks for retrieving and manipulating their characteristics.

-----

### images-resize

If you'd like to ensure your images have a standard size, or wish to have several
different sizes available so that you can send an appropriate size to a client,
`images-resize` is available to make this easier. `(images-resize)` is sufficient
for default behavior, and you can also set these options:

- `:out-dir` --- change the output directory from "public". It should not start with a
  slash, and can be nested, e.g. "foo/bar"
- `:resolutions` --- The numbers in this set define the width of the output file(s).
  default: `#{3840 2560 1920 1280 1024 640}`

-----

### images-dimensions

Sometimes, just knowing the size of your images is enough. `images-dimensions` sets
`:width` and `:height` keys in the metadata for your images, so that you can easily
retrieve them later. Usage is simply `(images-dimensions)`, and there are no options.

-----

## Utility Tasks

-----

### print-meta

Useful for debugging, `print-meta` will show all of the metadata that Perun knows for
the files in your fileset. For all metadata on all files, use `(print-meta)`. To
restrict the amount of information printed, use these options:

- `:map-fn` --- this function is applied to each metadata map before it is printed
- `:filterer` --- restrict the files that will be processed using this function, which
  will be passed to `clojure.core/filter`
- `:extensions` --- restrict the files that will be processed by passing a vector of file
  extensions, eg. `[".html" ".htm"]`
- `:content-exts` --- files with extensions in this set will have their contents printed
  along with their metadata

-----

\* Markdown extension descriptions are from https://github.com/sirthias/pegdown/blob/master/src/main/java/org/pegdown/Extensions.java

[markdown]: https://en.wikipedia.org/wiki/Markdown
