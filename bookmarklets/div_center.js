function centerAllDivs(current) {
  Array.from(current.children).forEach(centerAllDivs);
  if (current.tagName === "DIV") {
    current.style.setProperty("margin", "auto", "important");
    current.style.setProperty("align-self", "center", "important");
    current.style.setProperty("justify-self", "center", "important");
    current.style.setProperty("place-self", "center", "important");
  }
}
centerAllDivs(document);
