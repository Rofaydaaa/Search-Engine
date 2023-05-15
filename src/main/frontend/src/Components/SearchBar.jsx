import React, { useState, useEffect, useRef } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch, faSpinner, faTrash } from '@fortawesome/free-solid-svg-icons';
import { InputGroup } from 'react-bootstrap'
import api from '../api/axiosConfig';
import './Styles/SearchEngine.css';

function SearchBar(props) {
    const [deleteDisabled, setDeleteDisabled] = useState(true);
    const [searchDisabled, setSearchDisabled] = useState(true);
    const [searchText, setSearchText] = useState(props.searchText);
    const [searchIcon, setSearchIcon] = useState(faSearch);
    const [searchColor, setSearchColor] = useState('lightgray');
    const [deleteColor, setDeleteColor] = useState('lightgray');
    const [allSuggestions, setAllSuggestions] = useState([]);
    const [suggestions, setSuggestions] = useState([]);
    const [selectedSuggestionIndex, setSelectedSuggestionIndex] = useState(-1);
    const inputReference = useRef(null);


    const getSuggestions = async () => {
        try {
            const response = await api.get('/suggestions');
            setAllSuggestions(response.data);
            console.log(response.data);
        }
        catch (error) {
            console.log(error);
        }
    };

    const filterSuggestions = () => {
        const filteredSuggestions = allSuggestions.filter(suggestion => {
            return suggestion.suggestion.toLowerCase().startsWith(searchText.toLowerCase());
        });
        setSuggestions(filteredSuggestions);
        console.log(filteredSuggestions);
    };

    useEffect(() => {
        getSuggestions();
    }, []);

    useEffect(() => {
        setSearchText(props.searchText);
    }, [props.searchText]);

    useEffect(() => {
        filterSuggestions();
    }, [searchText]);

    useEffect(() => {
        if (searchText === null || searchText.trim() === '') {
            setDeleteDisabled(true);
            setSearchDisabled(true);
            setDeleteColor('lightgray');
            setSearchColor('lightgray');
        } else {
            setDeleteDisabled(false);
            setSearchDisabled(false);
            setDeleteColor('#433e3e');
            setSearchColor('#433e3e');
        }
        setSelectedSuggestionIndex(-1);
    }, [searchText, suggestions]);

    function handleOnFocus() {
        props.setFlag(true);
    }

    function handleOnBlur() {
        setTimeout(() => {
            props.setFlag(false);
        }, 500);
    }

    function handleKeyDown(event) {
        if (event.key === 'ArrowUp') {
            // move selection up
            setSelectedSuggestionIndex((prevIndex) => {
                const newIndex = prevIndex - 1;
                return newIndex < 0 ? suggestions.length - 1 : newIndex;
            });
        }
        else if (event.key === 'ArrowDown') {
            // move selection down
            setSelectedSuggestionIndex((prevIndex) => {
                const newIndex = prevIndex + 1;
                return newIndex >= suggestions.length ? 0 : newIndex;
            });
        }
        else if (event.key === 'Tab' && selectedSuggestionIndex >= 0) {
            event.preventDefault();
            const end = inputReference.current.selectionEnd;
            handleSelectSuggestion(suggestions[selectedSuggestionIndex]);
            setSearchText(searchText + suggestions[selectedSuggestionIndex].suggestion.substring(end));
            inputReference.current.focus();
        }
        else if (event.key === 'Enter' && selectedSuggestionIndex >= 0) {
            // select suggestion on Enter key press
            handleSelectSuggestion(suggestions[selectedSuggestionIndex]);
            setSearchText(suggestions[selectedSuggestionIndex].suggestion);
            props.handleSearch(suggestions[selectedSuggestionIndex].suggestion);
            setSearchText('');
            setSelectedSuggestionIndex(-1);
        }
        else if (event.key === 'Enter') {
            setSearchText(event.target.value);
            handleSearchButtonClick();
            setSearchText('');
            setSelectedSuggestionIndex(-1);
        }
    }

    function handleIconMouseEnter() {
        setSearchIcon(faSpinner);
    }

    function handleIconMouseLeave() {
        setSearchIcon(faSearch);
    }

    function handleDeleteMouseEnter() {
        setDeleteColor('#ca0000');
    }

    function handleDeleteMouseLeave() {
        if (deleteDisabled)
            setDeleteColor('lightgray');
        else
            setDeleteColor('#433e3e');
    }

    function handleSearch(e) {
        setSearchText(e.target.value);
        props.setFlag(true);
    }

    function handleDeleteSearch() {
        setSearchText('');
    }

    const addSuggestions = async() => {
        try {
            const response = await api.post('/suggestions', {suggestion: searchText});
            console.log(response.data);
        }
        catch (error) {
            console.log(error);
        }
    }

    function handleSearchButtonClick() {
        addSuggestions();
        props.handleSearch(searchText);
    }

    function renderSuggestions() {
        if (props.suggestionsFlag && suggestions.length > 0) {
            return (
                <ul className="suggestions">
                    {suggestions.map((suggestion, index) => (
                        <li
                            key={index}
                            className={`${index === selectedSuggestionIndex ? 'selected' : ''}`}
                            onClick={() => handleSelectSuggestion(suggestion.suggestion)}
                            onKeyDown={(e) => { if (e == 'Enter') { handleSelectSuggestion(suggestion.suggestion) } }}
                        >
                            {suggestion.suggestion}
                        </li>
                    ))}
                </ul>
            );
        } else {
            return null;
        }
    }


    function handleSelectSuggestion(suggestion) {
        setSearchText(suggestion);
        inputReference.current.value = suggestion;
        inputReference.current.focus();
    }


    return (
        <InputGroup className='searchBar'>
            <div className='searchFieldBlock'>
                <div>
                    <input
                        ref={inputReference}
                        className='searchField'
                        value={searchText}
                        onChange={handleSearch}
                        onKeyDown={handleKeyDown}
                        id='search-input'
                        type="text"
                        placeholder="Search..."
                        onFocus={handleOnFocus}
                        onBlur={handleOnBlur}
                    />
                </div>
                <div>
                    {!searchDisabled && renderSuggestions()}
                </div>
            </div>
            <button
                id='button1'
                disabled={searchDisabled}
                className='searchButton'
                onMouseEnter={handleIconMouseEnter}
                onMouseLeave={handleIconMouseLeave}
                onClick={handleSearchButtonClick}
            >
                <FontAwesomeIcon
                    className='searchIcon'
                    disabled={searchDisabled}
                    icon={searchIcon}
                    color={searchColor}
                />
            </button>
            <button
                id='button2'
                onClick={handleDeleteSearch}
                disabled={deleteDisabled}
                className='deleteButton'
                onMouseEnter={handleDeleteMouseEnter}
                onMouseLeave={handleDeleteMouseLeave}>
                <FontAwesomeIcon
                    className='deleteIcon'
                    icon={faTrash}
                    color={deleteColor}
                />
            </button>
        </InputGroup>
    );
}

export default SearchBar;