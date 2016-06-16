# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.0.3] - 2016-06-16
### Added
- `literal` modifier.
- Global config for implicit behaviors through dynamic variable `*config*` - currently support `:automap-seq`.

### Changed
- `from` modifier now support `:then` optional parameter for postprocessing and to work with `:automap-seq`.
- `from` modifier support `:automap?` optional parameter to override global config.

## [0.0.2] - 2016-06-04
### Added
- Support for nested map in target domain.
- `one-or-more` modifier.

## 0.0.1 - 2016-05-31
### Added
- Basic converter with `from` modifier.

[0.0.3]: https://github.com/lemonteaa/relabel/compare/v0.0.2-alpha...v0.0.3-alpha
[0.0.2]: https://github.com/lemonteaa/relabel/compare/v0.0.1-alpha...v0.0.2-alpha
