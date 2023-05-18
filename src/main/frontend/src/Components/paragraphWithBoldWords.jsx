import { stemmer } from 'stemmer';
import "./Styles/paragraphWithBoldWords.css"

function exactMatch(word, boldWords) {
    const stemmedWord = stemmer(word);
    for (let i = 0; i < boldWords.length; i++) {
        if (stemmedWord.match(new RegExp("\\b" + stemmer(boldWords[i]) + "\\b","i")) && stemmer(boldWords[i])) {
            console.log(stemmedWord);
            console.log(stemmer(boldWords[i]))
            return true;
        }
    }
    return false;
}

function paragraphWithBoldWords(paragraph, boldWords) {

    boldWords = boldWords.replace(/"/g, '');

    const boldArr = boldWords.split(' ');
    // Split the paragraph into an array of words and map over it
    const boldParagraph = paragraph.split(" ").map((word) => {
        // If the word is included in the boldWords array, wrap it in a b element
        if (exactMatch(word, boldArr)) {
            return <b className="bold">{word} </b>;
        }
        // Otherwise, return the word as a normal text node
        return word + " ";
    });
    // Join the array of words back into a string and render it inside a p element
    return <p className="boldP">{boldParagraph}</p>;
}

export default paragraphWithBoldWords;
