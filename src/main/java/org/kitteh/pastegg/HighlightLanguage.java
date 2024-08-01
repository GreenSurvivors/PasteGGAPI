/*
 * * Copyright (C) 2018-2020 Matt Baxter https://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.pastegg;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Locale;

public enum HighlightLanguage {
    Abnf,
    AccessLog,
    ActionScript,
    Ada,
    Apache,
    AppleScript,
    Arduino,
    ArmAsm,
    AsciiDoc,
    AspectJ,
    AutoHotKey,
    AutoIt,
    AvrAsm,
    Awk,
    Axapta,
    Bash,
    Basic,
    Bnf,
    Brainfuck,
    CPlusPlus,
    CSharp,
    Cal,
    CapnProto,
    Ceylon,
    Clean,
    Clojure,
    ClojureRepl,
    Cmake,
    CoffeeScript,
    Coq,
    Cos,
    Crmsh,
    Crystal,
    Csp,
    Css,
    D,
    Dart,
    Delphi,
    Diff,
    Django,
    Dns,
    Dockerfile,
    Dos,
    DsConfig,
    Dts,
    Dust,
    Ebnf,
    Elixir,
    Elm,
    EmbeddedRuby,
    Erlang,
    ErlangRepl,
    Excel,
    FSharp,
    Fix,
    Flix,
    Fortran,
    GCode,
    Gams,
    Gauss,
    Gherkin,
    Glsl,
    Go,
    Golo,
    Gradle,
    Groovy,
    Haml,
    Handlebars,
    Haskell,
    Haxe,
    Hsp,
    Htmlbars,
    Http,
    Hy,
    Inform7,
    Ini,
    Irpf90,
    Java,
    JavaScript,
    JbossCli,
    Json,
    Julia,
    JuliaRepl,
    Kotlin,
    Lasso,
    Ldif,
    Leaf,
    Less,
    LindenScriptingLanguage,
    Lisp,
    LiveCodeServer,
    LiveScript,
    Llvm,
    Lua,
    Makefile,
    Markdown,
    Mathematica,
    Matlab,
    Maxima,
    Mel,
    Mercury,
    MipsAsm,
    Mizar,
    Mojolicious,
    Monkey,
    MoonScript,
    N1ql,
    Nginx,
    Nimrod,
    Nix,
    Nsis,
    ObjectiveC,
    Ocaml,
    OneC,
    OpenScad,
    Oxygene,
    Parser3,
    Perl,
    Pf,
    Php,
    Pony,
    PowerShell,
    Processing,
    Profile,
    Prolog,
    ProtocolBuffers,
    Puppet,
    PureBasic,
    Python,
    Q,
    Qml,
    R,
    Rib,
    Roboconf,
    RouterOs,
    Rsl,
    Ruby,
    RulesLanguage,
    Rust,
    Scala,
    Scheme,
    Scilab,
    Scss,
    Shell,
    Smali,
    Smalltalk,
    Sqf,
    Sql,
    Stan,
    StandardMl,
    Stata,
    Step21,
    Stylus,
    Subunit,
    Swift,
    TaggerScript,
    Tap,
    Tcl,
    Tex,
    Thrift,
    Tp,
    Twig,
    Typescript,
    Vala,
    VbNet,
    VbScript,
    VbScriptHtml,
    Verilog,
    Vhdl,
    Vim,
    X86Asm,
    XQuery,
    Xl,
    Xml,
    Yaml,
    Zephir;

    public static final EnumSet<HighlightLanguage> VALUES = EnumSet.allOf(HighlightLanguage.class);

    public static class TypeAdapter implements JsonSerializer<HighlightLanguage>, JsonDeserializer<HighlightLanguage> {
        @Override
        public @NotNull HighlightLanguage deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString();
            for (HighlightLanguage language : VALUES) {
                if (language.toString().equalsIgnoreCase(name)) {
                    return language;
                }
            }

            throw new JsonParseException("Unknown language \"" + name + "\"");
        }

        @Override
        public @NotNull JsonElement serialize(@NotNull HighlightLanguage src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString().toLowerCase(Locale.ENGLISH));
        }
    }
}
