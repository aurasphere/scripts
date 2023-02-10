function removeDivsIgnoreChildren(current) {
  Array.from(current.children).forEach(removeDivsIgnoreChildren);
  if (current.tagName === "DIV") {
    Array.from(current.children).forEach((child) =>
      current.parentNode.appendChild(child)
    );
    current.remove();
  }
}
removeDivsIgnoreChildren(document);
