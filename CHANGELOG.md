# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.1.0] - 2017-07-17
### Added
- Support for [Specter](https://github.com/nathanmarz/specter) path in `from` modifier.
- Strict/Loose mode in global config. In loose mode unmatched values fallback to using default (`nil` or the value in `:default` optional parameter) instead of throwing an Exception.

### Changed
- **(Breaking Change)** From now on, Exceptions thrown by this library itself should be done through `ex-info`.

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

[0.1.0]: https://github.com/lemonteaa/relabel/compare/v0.0.3-alpha...v0.1.0-stable
[0.0.3]: https://github.com/lemonteaa/relabel/compare/v0.0.2-alpha...v0.0.3-alpha
[0.0.2]: https://github.com/lemonteaa/relabel/compare/v0.0.1-alpha...v0.0.2-alpha
