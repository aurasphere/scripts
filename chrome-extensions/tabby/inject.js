(function () {
  function codeToInject() {
    console.log("Code injected from extension!");
  }

  function inject(fn) {
    const script = document.createElement("script");
    script.text = `(${fn.toString()})();`;
    document.documentElement.appendChild(script);
  }

  inject(codeToInject);
})();