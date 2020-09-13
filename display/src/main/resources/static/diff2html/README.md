# diff2html

[![Codacy Quality Badge](https://api.codacy.com/project/badge/Grade/06412dc3f5a14f568778d0db8a1f7dc8)](https://www.codacy.com/app/rtfpessoa/diff2html?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=rtfpessoa/diff2html&amp;utm_campaign=Badge_Grade)
[![Codacy Coverage Badge](https://api.codacy.com/project/badge/Coverage/06412dc3f5a14f568778d0db8a1f7dc8)](https://www.codacy.com/app/rtfpessoa/diff2html?utm_source=github.com&utm_medium=referral&utm_content=rtfpessoa/diff2html&utm_campaign=Badge_Coverage)
[![Circle CI](https://circleci.com/gh/rtfpessoa/diff2html.svg?style=svg)](https://circleci.com/gh/rtfpessoa/diff2html)
[![Dependency Status](https://dependencyci.com/github/rtfpessoa/diff2html/badge)](https://dependencyci.com/github/rtfpessoa/diff2html)

[![npm](https://img.shields.io/npm/v/diff2html.svg)](https://www.npmjs.com/package/diff2html)
[![Dependency Status](https://david-dm.org/rtfpessoa/diff2html.svg)](https://david-dm.org/rtfpessoa/diff2html)
[![devDependency Status](https://david-dm.org/rtfpessoa/diff2html/dev-status.svg)](https://david-dm.org/rtfpessoa/diff2html#info=devDependencies)
[![cdnjs](https://img.shields.io/cdnjs/v/diff2html)](https://cdnjs.com/libraries/diff2html)

[![node](https://img.shields.io/node/v/diff2html.svg)]()
[![npm](https://img.shields.io/npm/l/diff2html.svg)]()
[![npm](https://img.shields.io/npm/dm/diff2html.svg)](https://www.npmjs.com/package/diff2html)
[![All Contributors](https://img.shields.io/badge/all_contributors-22-orange.svg?style=flat-square)](#contributors-)
[![Gitter](https://badges.gitter.im/rtfpessoa/diff2html.svg)](https://gitter.im/rtfpessoa/diff2html?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

diff2html generates pretty HTML diffs from git or unified diff output.

[![NPM](https://nodei.co/npm/diff2html.png?downloads=true&downloadRank=true&stars=true)](https://nodei.co/npm/diff2html/)

## Features

* Supports git and unified diffs

* Line by line and Side by side diff

* New and old line numbers

* Inserted and removed lines

* GitHub like style

* Code syntax highlight

* Line similarity matching

* Easy code selection

## Online Example

> Go to [diff2html](https://diff2html.xyz/)

## Distributions

* [WebJar](http://www.webjars.org/)

* [Node Module](https://www.npmjs.org/package/diff2html)

* [Bower Package](http://bower.io/search/?q=diff2html)

* [Node CLI](https://www.npmjs.org/package/diff2html-cli)

* Manually download and import [dist/diff2html.min.js](./dist/diff2html.min.js) into your page

## How to use

To load correctly in the Browser you always need to include the stylesheet in the final HTML.

Import the stylesheet

```html
<!-- CSS -->
<link rel="stylesheet" type="text/css" href="dist/diff2html.css">
```

You can also refer to it from a CDN like [CDNJS](https://cdnjs.com/libraries/diff2html).

### Browser Library

Import the stylesheet and the library code

```html
<!-- CSS -->
<link rel="stylesheet" type="text/css" href="dist/diff2html.css">

<!-- Javascripts -->
<script type="text/javascript" src="dist/diff2html.js"></script>
```

It will now be available as a global variable named `Diff2Html`.

```js
var diffHtml = Diff2Html.getPrettyHtml(
  '<Unified Diff String>',
  {inputFormat: 'diff', showFiles: true, matching: 'lines', outputFormat: 'side-by-side'}
);
document.getElementById("destination-elem-id").innerHTML = diffHtml;
```

### Node Module

```js
let diff2html = require("diff2html").Diff2Html
```

### Angular

* Typescript

```typescript
// import diff2html
import {Diff2Html} from 'diff2html'
import {Component, OnInit} from '@angular/core';


export class AppDiffComponent implements OnInit {
  outputHtml: string;
  constructor() {
    this.init();
  }

  ngOnInit() {
  }

  init() {
    let strInput = "--- a/server/vendor/golang.org/x/sys/unix/zsyscall_linux_mipsle.go\n+++ b/server/vendor/golang.org/x/sys/unix/zsyscall_linux_mipsle.go\n@@ -1035,6 +1035,17 @@ func Prctl(option int, arg2 uintptr, arg3 uintptr, arg4 uintptr, arg5 uintptr) (\n \n // THIS FILE IS GENERATED BY THE COMMAND AT THE TOP; DO NOT EDIT\n \n+func Pselect(nfd int, r *FdSet, w *FdSet, e *FdSet, timeout *Timespec, sigmask *Sigset_t) (n int, err error) {\n+\tr0, _, e1 := Syscall6(SYS_PSELECT6, uintptr(nfd), uintptr(unsafe.Pointer(r)), uintptr(unsafe.Pointer(w)), uintptr(unsafe.Pointer(e)), uintptr(unsafe.Pointer(timeout)), uintptr(unsafe.Pointer(sigmask)))\n+\tn = int(r0)\n+\tif e1 != 0 {\n+\t\terr = errnoErr(e1)\n+\t}\n+\treturn\n+}\n+\n+// THIS FILE IS GENERATED BY THE COMMAND AT THE TOP; DO NOT EDIT\n+\n func read(fd int, p []byte) (n int, err error) {\n \tvar _p0 unsafe.Pointer\n \tif len(p) > 0 {\n";
    let outputHtml = Diff2Html.getPrettyHtml(strInput, {inputFormat: 'diff', showFiles: true, matching: 'lines'});
    this.outputHtml = outputHtml;
  }
}
```

* HTML

```html
<!DOCTYPE html>
<html>
  <head>
    <title>diff2html</title>
  </head>
  <body>
    <div [innerHtml]="outputHtml"></div>
  </body>
</html>
```

* `.angular-cli.json` - Add styles

```json
"styles": [
  "diff2html.min.css"
]
```

### Vue.js

```vue
<template>
    <div v-html="prettyHtml" />
</template>

<script>
import { Diff2Html } from "diff2html";
import "diff2html/dist/diff2html.min.css";

export default {
  data() {
    return {
      diffs: "--- a/server/vendor/golang.org/x/sys/unix/zsyscall_linux_mipsle.go\n+++ b/server/vendor/golang.org/x/sys/unix/zsyscall_linux_mipsle.go\n@@ -1035,6 +1035,17 @@ func Prctl(option int, arg2 uintptr, arg3 uintptr, arg4 uintptr, arg5 uintptr) (\n \n // THIS FILE IS GENERATED BY THE COMMAND AT THE TOP; DO NOT EDIT\n \n+func Pselect(nfd int, r *FdSet, w *FdSet, e *FdSet, timeout *Timespec, sigmask *Sigset_t) (n int, err error) {\n+\tr0, _, e1 := Syscall6(SYS_PSELECT6, uintptr(nfd), uintptr(unsafe.Pointer(r)), uintptr(unsafe.Pointer(w)), uintptr(unsafe.Pointer(e)), uintptr(unsafe.Pointer(timeout)), uintptr(unsafe.Pointer(sigmask)))\n+\tn = int(r0)\n+\tif e1 != 0 {\n+\t\terr = errnoErr(e1)\n+\t}\n+\treturn\n+}\n+\n+// THIS FILE IS GENERATED BY THE COMMAND AT THE TOP; DO NOT EDIT\n+\n func read(fd int, p []byte) (n int, err error) {\n \tvar _p0 unsafe.Pointer\n \tif len(p) > 0 {\n"
    };
  },
  computed: {
    prettyHtml() {
      return Diff2Html.getPrettyHtml(this.diffs, {
          inputFormat: "diff",
          showFiles: true,
          matching: "lines",
          outputFormat: "side-by-side"
        });
    }
  }
};
</script>
```

## API

> Intermediate Json From Git Word Diff Output

    getJsonFromDiff(input: string, configuration?: Options): Result[]

> Pretty HTML diff

    getPrettyHtml(input: any, configuration?: Options): string

> Check out the [src/diff2html.d.ts](./src/diff2html.d.ts) for a complete API definition in TypeScript.

> Check out the [docs/demo.html](./docs/demo.html) for a demo example.

## Configuration
The HTML output accepts a Javascript object with configuration. Possible options:

  - `inputFormat`: the format of the input data: `'diff'` or `'json'`, default is `'diff'`
  - `outputFormat`: the format of the output data: `'line-by-line'` or `'side-by-side'`, default is `'line-by-line'`
  - `showFiles`: show a file list before the diff: `true` or `false`, default is `false`
  - `diffStyle`: show differences level in each line: `word` or `char`, default is `word`
  - `matching`: matching level: `'lines'` for matching lines, `'words'` for matching lines and words or `'none'`, default is `none`
  - `matchWordsThreshold`: similarity threshold for word matching, default is 0.25
  - `matchingMaxComparisons`: perform at most this much comparisons for line matching a block of changes, default is `2500`
  - `maxLineSizeInBlockForComparison`: maximum number os characters of the bigger line in a block to apply comparison, default is `200`
  - `maxLineLengthHighlight`: only perform diff changes highlight if lines are smaller than this, default is `10000`
  - `templates`: object with previously compiled templates to replace parts of the html
  - `rawTemplates`: object with raw not compiled templates to replace parts of the html
  - `renderNothingWhenEmpty`: render nothing if the diff shows no change in its comparison: `true` or `false`, default is `false`
  > For more information regarding the possible templates look into [src/templates](https://github.com/rtfpessoa/diff2html/tree/master/src/templates)

** Diff2HtmlUI Helper Options **
  - `synchronisedScroll`: scroll both panes in side-by-side mode: `true` or `false`, default is `false`

> For more information regarding the possible templates look into [src/templates](https://github.com/rtfpessoa/diff2html/tree/master/src/templates)


## Diff2HtmlUI Helper

> Simple wrapper to ease simple tasks in the browser such as: code highlight and js effects

* Invoke Diff2html
* Inject output in DOM element
* Enable collapsible file summary list
* Enable syntax highlight of the code in the diffs

### How to use

#### Mandatory HTML resource imports

```html
<!-- CSS -->
<link rel="stylesheet" type="text/css" href="dist/diff2html.css">

<!-- Javascripts -->
<script type="text/javascript" src="dist/diff2html.js"></script>
<script type="text/javascript" src="dist/diff2html-ui.js"></script>
```

#### Init

```js
var diff2htmlUi = new Diff2HtmlUI({diff: diffString});
// or
var diff2htmlUi = new Diff2HtmlUI({json: diffJson});
```

#### Draw

```js
diff2htmlUi.draw('html-target-elem', {inputFormat: 'json', showFiles: true, matching: 'lines'});
```

#### Syntax Highlight

> Add the dependencies.
Choose one color scheme, and add the main highlight code. Note that the stylesheet for the color scheme must come **before** the main diff2html stylesheet.
If your favourite language is not included in the default package also add its javascript highlight file.

```html
<!-- Stylesheet -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/github.min.css">
<link rel="stylesheet" type="text/css" href="dist/diff2html.css">

<!-- Javascripts -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.3/jquery.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/highlight.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/languages/scala.min.js"></script>
<script type="text/javascript" src="dist/diff2html-ui.js"></script>
```

> Invoke the Diff2HtmlUI helper

```js
$(document).ready(function() {
    var diff2htmlUi = new Diff2HtmlUI({diff: lineDiffExample});
    diff2htmlUi.draw('#line-by-line', {inputFormat: 'json', showFiles: true, matching: 'lines'});
    diff2htmlUi.highlightCode('#line-by-line');
});
```

#### Collapsable File Summary List

> Add the dependencies.

```html
<!-- Javascripts -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.3/jquery.js"></script>
<script type="text/javascript" src="dist/diff2html-ui.js"></script>
```

> Invoke the Diff2HtmlUI helper

```js
$(document).ready(function() {
    var diff2htmlUi = new Diff2HtmlUI({diff: lineDiffExample});
    diff2htmlUi.draw('#line-by-line', {inputFormat: 'json', showFiles: true, matching: 'lines'});
    diff2htmlUi.fileListCloseable('#line-by-line', false);
});
```

# Troubleshooting

### 1. Out of memory or Slow execution

#### Causes:
* Big files
* Big lines

#### Fix:
* Disable the line matching algorithm, by setting the option `{"matching": "none"}` when invoking diff2html

## Contributions

This is a developer friendly project, all the contributions are welcome.
To contribute just send a pull request with your changes following the guidelines described in `CONTRIBUTING.md`.
I will try to review them as soon as possible.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://rtfpessoa.xyz"><img src="https://avatars0.githubusercontent.com/u/902384?v=4" width="100px;" alt="Rodrigo Fernandes"/><br /><sub><b>Rodrigo Fernandes</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=rtfpessoa" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/stockmind"><img src="https://avatars3.githubusercontent.com/u/5653847?v=4" width="100px;" alt="stockmind"/><br /><sub><b>stockmind</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=stockmind" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/lantian"><img src="https://avatars3.githubusercontent.com/u/535545?v=4" width="100px;" alt="Ivan Vorontsov"/><br /><sub><b>Ivan Vorontsov</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=lantian" title="Code">💻</a></td>
    <td align="center"><a href="http://www.nick-brewer.com"><img src="https://avatars1.githubusercontent.com/u/129300?v=4" width="100px;" alt="Nick Brewer"/><br /><sub><b>Nick Brewer</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=brewern" title="Code">💻</a></td>
    <td align="center"><a href="http://heyitsmattwade.com"><img src="https://avatars0.githubusercontent.com/u/8504000?v=4" width="100px;" alt="Matt Wade"/><br /><sub><b>Matt Wade</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3Aromellem" title="Bug reports">🐛</a></td>
    <td align="center"><a href="http://mrfyda.github.io"><img src="https://avatars1.githubusercontent.com/u/593860?v=4" width="100px;" alt="Rafael Cortês"/><br /><sub><b>Rafael Cortês</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=mrfyda" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/nmatpt"><img src="https://avatars2.githubusercontent.com/u/5034733?v=4" width="100px;" alt="Nuno Teixeira"/><br /><sub><b>Nuno Teixeira</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=nmatpt" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://saino.me/"><img src="https://avatars0.githubusercontent.com/u/1567423?v=4" width="100px;" alt="Koki Oyatsu"/><br /><sub><b>Koki Oyatsu</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3Akaishuu0123" title="Bug reports">🐛</a></td>
    <td align="center"><a href="http://www.jamesmonger.com"><img src="https://avatars2.githubusercontent.com/u/2037007?v=4" width="100px;" alt="James Monger"/><br /><sub><b>James Monger</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=Jameskmonger" title="Documentation">📖</a></td>
    <td align="center"><a href="http://wesssel.github.io/"><img src="https://avatars2.githubusercontent.com/u/7767299?v=4" width="100px;" alt="Wessel van der Pal"/><br /><sub><b>Wessel van der Pal</b></sub></a><br /><a href="#security-wesssel" title="Security">🛡️</a></td>
    <td align="center"><a href="https://jung-kim.github.io"><img src="https://avatars2.githubusercontent.com/u/5281068?v=4" width="100px;" alt="jk-kim"/><br /><sub><b>jk-kim</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=jung-kim" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/sss0791"><img src="https://avatars1.githubusercontent.com/u/1446970?v=4" width="100px;" alt="Sergey Semenov"/><br /><sub><b>Sergey Semenov</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3Asss0791" title="Bug reports">🐛</a></td>
    <td align="center"><a href="http://researcher.watson.ibm.com/researcher/view.php?person=us-nickm"><img src="https://avatars3.githubusercontent.com/u/4741620?v=4" width="100px;" alt="Nick Mitchell"/><br /><sub><b>Nick Mitchell</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3Astarpit" title="Bug reports">🐛</a></td>
    <td align="center"><a href="https://github.com/samiraguiar"><img src="https://avatars0.githubusercontent.com/u/13439135?v=4" width="100px;" alt="Samir Aguiar"/><br /><sub><b>Samir Aguiar</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=samiraguiar" title="Documentation">📖</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://twitter.com/pubkeypubkey"><img src="https://avatars3.githubusercontent.com/u/8926560?v=4" width="100px;" alt="pubkey"/><br /><sub><b>pubkey</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=pubkey" title="Documentation">📖</a></td>
    <td align="center"><a href="https://github.com/iliyaZelenko"><img src="https://avatars1.githubusercontent.com/u/13103045?v=4" width="100px;" alt="Илья"/><br /><sub><b>Илья</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=iliyaZelenko" title="Documentation">📖</a></td>
    <td align="center"><a href="https://akr.am"><img src="https://avatars0.githubusercontent.com/u/1823771?v=4" width="100px;" alt="Mohamed Akram"/><br /><sub><b>Mohamed Akram</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3Amohd-akram" title="Bug reports">🐛</a></td>
    <td align="center"><a href="https://github.com/emarcotte"><img src="https://avatars0.githubusercontent.com/u/249390?v=4" width="100px;" alt="Eugene Marcotte"/><br /><sub><b>Eugene Marcotte</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=emarcotte" title="Code">💻</a></td>
    <td align="center"><a href="http://twitter.com/dimasabanin"><img src="https://avatars0.githubusercontent.com/u/8316?v=4" width="100px;" alt="Dima Sabanin"/><br /><sub><b>Dima Sabanin</b></sub></a><br /><a href="#maintenance-dsabanin" title="Maintenance">🚧</a></td>
    <td align="center"><a href="https://github.com/benabbottnz"><img src="https://avatars2.githubusercontent.com/u/2616473?v=4" width="100px;" alt="Ben Abbott"/><br /><sub><b>Ben Abbott</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/commits?author=benabbottnz" title="Documentation">📖</a></td>
    <td align="center"><a href="http://webminer.js.org"><img src="https://avatars1.githubusercontent.com/u/2196373?v=4" width="100px;" alt="弘树@阿里"/><br /><sub><b>弘树@阿里</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3Adickeylth" title="Bug reports">🐛</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/Rantanen"><img src="https://avatars0.githubusercontent.com/u/385385?v=4" width="100px;" alt="Mikko Rantanen"/><br /><sub><b>Mikko Rantanen</b></sub></a><br /><a href="https://github.com/rtfpessoa/diff2html/issues?q=author%3ARantanen" title="Bug reports">🐛</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

## License

Copyright 2014-2016 Rodrigo Fernandes. Released under the terms of the MIT license.

## Thanks

This project is inspired in [pretty-diff](https://github.com/scottgonzalez/pretty-diff) by [Scott González](https://github.com/scottgonzalez).

---